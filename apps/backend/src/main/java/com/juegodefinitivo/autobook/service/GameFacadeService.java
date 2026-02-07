package com.juegodefinitivo.autobook.service;

import com.juegodefinitivo.autobook.api.dto.BookView;
import com.juegodefinitivo.autobook.api.dto.ChallengeView;
import com.juegodefinitivo.autobook.api.dto.GameStateResponse;
import com.juegodefinitivo.autobook.api.dto.NarrativeGraphResponse;
import com.juegodefinitivo.autobook.api.dto.QuestView;
import com.juegodefinitivo.autobook.api.dto.SceneView;
import com.juegodefinitivo.autobook.config.AppConfig;
import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.engine.PlayerAction;
import com.juegodefinitivo.autobook.engine.TurnOutcome;
import com.juegodefinitivo.autobook.ingest.BookAsset;
import com.juegodefinitivo.autobook.ingest.BookCatalogService;
import com.juegodefinitivo.autobook.ingest.BookImportService;
import com.juegodefinitivo.autobook.ingest.BookLoaderService;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import com.juegodefinitivo.autobook.narrative.NarrativeScene;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameFacadeService {

    private final AppConfig config;
    private final BookCatalogService catalog;
    private final BookImportService importer;
    private final BookLoaderService loader;
    private final NarrativeBuilder narrativeBuilder;
    private final GameEngineService engine;
    private final AutoplayService autoplayService;
    private final NarrativeGraphService narrativeGraphService;

    private final Map<String, SessionState> sessions = new ConcurrentHashMap<>();

    public GameFacadeService(
            AppConfig config,
            BookCatalogService catalog,
            BookImportService importer,
            BookLoaderService loader,
            NarrativeBuilder narrativeBuilder,
            GameEngineService engine,
            AutoplayService autoplayService,
            NarrativeGraphService narrativeGraphService
    ) {
        this.config = config;
        this.catalog = catalog;
        this.importer = importer;
        this.loader = loader;
        this.narrativeBuilder = narrativeBuilder;
        this.engine = engine;
        this.autoplayService = autoplayService;
        this.narrativeGraphService = narrativeGraphService;
    }

    public void bootstrapSamples() {
        copySampleIfMissing("books/caballero-union.txt", "caballero-union.txt");
        copySampleIfMissing("books/capitulo1.txt", "capitulo1.txt");
    }

    public List<BookView> listBooks() {
        List<BookView> books = new ArrayList<>();
        for (BookAsset asset : catalog.listBooks()) {
            books.add(new BookView(asset.title(), asset.path().toString(), asset.format()));
        }
        return books;
    }

    public BookView importBook(String rawPath) {
        BookAsset asset = importer.importFromInput(rawPath);
        return new BookView(asset.title(), asset.path().toString(), asset.format());
    }

    public GameStateResponse startGame(String playerName, String bookPath) {
        Path path = Path.of(bookPath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("El libro no existe en la ruta enviada.");
        }

        GameSession session = new GameSession();
        session.setPlayerName(playerName == null || playerName.isBlank() ? "Aventurero" : playerName.trim());
        session.setBookPath(path.toString());
        session.setBookTitle(path.getFileName().toString());

        List<NarrativeScene> scenes = narrativeBuilder.build(loader.loadScenes(path, config.sceneMaxChars(), config.sceneLinesPerChunk()));
        if (scenes.isEmpty()) {
            throw new IllegalArgumentException("No se pudieron generar escenas jugables para ese libro.");
        }

        String id = UUID.randomUUID().toString();
        SessionState boot = new SessionState(session, scenes, "Partida iniciada.", new LinkedHashMap<>(), 0, 0);
        if (!scenes.isEmpty()) {
            boot = absorbEntities(id, boot, scenes.get(0));
        }
        sessions.put(id, boot);
        return toResponse(id, sessions.get(id));
    }

    public GameStateResponse getState(String sessionId) {
        SessionState state = requireSession(sessionId);
        return toResponse(sessionId, state);
    }

    public NarrativeGraphResponse getNarrativeGraph(String sessionId) {
        requireSession(sessionId);
        return narrativeGraphService.snapshot(sessionId);
    }

    public GameStateResponse applyAction(String sessionId, String actionValue, Integer answerIndex, String itemId) {
        SessionState state = requireSession(sessionId);
        GameSession session = state.session();

        if (session.isCompleted()) {
            return toResponse(sessionId, state.withMessage("La partida ya finalizo."));
        }

        if (session.getCurrentScene() >= state.scenes().size()) {
            session.setCompleted(true);
            return toResponse(sessionId, state.withMessage("No hay mas escenas disponibles."));
        }

        NarrativeScene scene = state.scenes().get(session.getCurrentScene());
        PlayerAction action = parseAction(actionValue);
        boolean challengeCorrect = false;
        if (action == PlayerAction.CHALLENGE) {
            if (answerIndex == null) {
                throw new IllegalArgumentException("Debes enviar answerIndex para el reto.");
            }
            challengeCorrect = scene.challengeQuestion().isCorrect(answerIndex);
            state = state.withChallengeAttempt(challengeCorrect);
        }

        TurnOutcome outcome = engine.apply(session, scene, action, challengeCorrect, itemId);
        state = absorbEntities(sessionId, state, scene);
        if (outcome.consumedTurn()) {
            engine.moveNextScene(session, state.scenes().size());
        }

        SessionState updated = state.withMessage(outcome.message());
        sessions.put(sessionId, updated);
        return toResponse(sessionId, updated);
    }

    public GameStateResponse applyAutoplay(String sessionId, String ageBand, String readingLevel, Integer maxSteps) {
        SessionState state = requireSession(sessionId);
        GameSession session = state.session();

        int steps = maxSteps == null ? 3 : Math.max(1, Math.min(15, maxSteps));
        int executed = 0;
        String lastMessage = "Modo auto sin movimientos.";

        for (int i = 0; i < steps; i++) {
            if (session.isCompleted()) {
                break;
            }
            if (session.getCurrentScene() >= state.scenes().size()) {
                session.setCompleted(true);
                break;
            }

            NarrativeScene scene = state.scenes().get(session.getCurrentScene());
            String effectiveReadingLevel = (readingLevel == null || readingLevel.isBlank())
                    ? state.adaptiveDifficulty().toLowerCase()
                    : readingLevel;
            AutoplayService.PlannedTurn planned = autoplayService.planTurn(session, scene, ageBand, effectiveReadingLevel);
            TurnOutcome outcome = engine.apply(session, scene, planned.action(), planned.challengeCorrect(), planned.itemId());
            state = absorbEntities(sessionId, state, scene);
            if (planned.action() == PlayerAction.CHALLENGE) {
                state = state.withChallengeAttempt(planned.challengeCorrect());
            }
            if (outcome.consumedTurn()) {
                engine.moveNextScene(session, state.scenes().size());
            }
            executed++;
            lastMessage = planned.rationale() + " " + outcome.message();
            state = state.withMessage(lastMessage);
            sessions.put(sessionId, state);
        }

        if (executed > 0) {
            state = state.withMessage(lastMessage + " (auto-steps=" + executed + ")");
            sessions.put(sessionId, state);
        }

        return toResponse(sessionId, state);
    }

    private PlayerAction parseAction(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("action es requerido.");
        }
        try {
            return PlayerAction.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("action invalida. Usa TALK, EXPLORE, CHALLENGE o USE_ITEM.");
        }
    }

    private SessionState requireSession(String id) {
        SessionState state = sessions.get(id);
        if (state == null) {
            throw new IllegalArgumentException("Sesion no encontrada.");
        }
        return state;
    }

    private GameStateResponse toResponse(String id, SessionState state) {
        GameSession session = state.session();
        SceneView sceneView = null;
        if (session.getCurrentScene() < state.scenes().size()) {
            NarrativeScene current = state.scenes().get(session.getCurrentScene());
            sceneView = new SceneView(
                    current.index(),
                    state.scenes().size(),
                    current.title(),
                    current.text(),
                    current.eventType().name(),
                    current.npc(),
                    new ChallengeView(current.challengeQuestion().prompt(), current.challengeQuestion().options()),
                    current.entities(),
                    current.cognitiveLevel(),
                    current.continuityHint()
            );
        }

        List<QuestView> quests = session.evaluateQuests().stream()
                .map(q -> new QuestView(q.id(), q.title(), q.description(), q.completed()))
                .toList();

        return new GameStateResponse(
                id,
                session.getPlayerName(),
                session.getBookTitle(),
                session.isCompleted(),
                session.getLife(),
                session.getKnowledge(),
                session.getCourage(),
                session.getFocus(),
                session.getScore(),
                session.getCorrectAnswers(),
                session.getDiscoveries(),
                session.getInventory(),
                state.narrativeMemory(),
                state.adaptiveDifficulty(),
                quests,
                sceneView,
                state.lastMessage()
        );
    }

    private SessionState absorbEntities(String sessionId, SessionState state, NarrativeScene scene) {
        Map<String, Integer> memory = new LinkedHashMap<>(state.narrativeMemory());
        for (String entity : scene.entities()) {
            String key = entity.trim();
            if (!key.isBlank()) {
                memory.merge(key, 1, Integer::sum);
            }
        }
        narrativeGraphService.record(sessionId, scene.entities());
        return state.withMemory(memory);
    }

    private void copySampleIfMissing(String resource, String name) {
        Path target = config.booksDir().resolve(name);
        if (Files.exists(target)) {
            return;
        }
        try {
            Files.createDirectories(config.booksDir());
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
                if (in != null) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private record SessionState(
            GameSession session,
            List<NarrativeScene> scenes,
            String lastMessage,
            Map<String, Integer> narrativeMemory,
            int challengeAttempts,
            int challengeCorrect
    ) {
        private SessionState withMessage(String message) {
            return new SessionState(session, scenes, message, narrativeMemory, challengeAttempts, challengeCorrect);
        }

        private SessionState withMemory(Map<String, Integer> memory) {
            return new SessionState(session, scenes, lastMessage, memory, challengeAttempts, challengeCorrect);
        }

        private SessionState withChallengeAttempt(boolean wasCorrect) {
            int nextAttempts = challengeAttempts + 1;
            int nextCorrect = challengeCorrect + (wasCorrect ? 1 : 0);
            return new SessionState(session, scenes, lastMessage, narrativeMemory, nextAttempts, nextCorrect);
        }

        private String adaptiveDifficulty() {
            if (challengeAttempts < 2) {
                return "INTERMEDIATE";
            }
            double accuracy = challengeCorrect / (double) challengeAttempts;
            if (accuracy < 0.45 || session.getLife() < 35) {
                return "BEGINNER";
            }
            if (accuracy > 0.8 && session.getScore() >= 80) {
                return "ADVANCED";
            }
            return "INTERMEDIATE";
        }
    }
}
