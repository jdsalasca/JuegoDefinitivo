package com.juegodefinitivo.autobook.ui;

import com.juegodefinitivo.autobook.config.AppConfig;
import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.domain.InventoryItem;
import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.domain.StoryQuest;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.engine.PlayerAction;
import com.juegodefinitivo.autobook.engine.TurnOutcome;
import com.juegodefinitivo.autobook.ingest.BookAsset;
import com.juegodefinitivo.autobook.ingest.BookCatalogService;
import com.juegodefinitivo.autobook.ingest.BookImportService;
import com.juegodefinitivo.autobook.ingest.BookLoaderService;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import com.juegodefinitivo.autobook.narrative.NarrativeScene;
import com.juegodefinitivo.autobook.persistence.SaveGameRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.io.InputStream;

public class ConsoleGameUI {

    private final AppConfig config;
    private final SaveGameRepository repository;
    private final BookCatalogService catalog;
    private final BookImportService importer;
    private final BookLoaderService loader;
    private final NarrativeBuilder narrativeBuilder;
    private final GameEngineService gameEngine;
    private final Scanner scanner;

    public ConsoleGameUI(
            AppConfig config,
            SaveGameRepository repository,
            BookCatalogService catalog,
            BookImportService importer,
            BookLoaderService loader,
            NarrativeBuilder narrativeBuilder,
            GameEngineService gameEngine,
            Scanner scanner
    ) {
        this.config = config;
        this.repository = repository;
        this.catalog = catalog;
        this.importer = importer;
        this.loader = loader;
        this.narrativeBuilder = narrativeBuilder;
        this.gameEngine = gameEngine;
        this.scanner = scanner;
    }

    public void run() {
        ensureSamplesVisible();

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("====== " + config.appName() + " ======");
            System.out.println("1. Nueva partida");
            System.out.println("2. Continuar partida");
            System.out.println("3. Importar libro (.txt/.pdf)");
            System.out.println("4. Ver catalogo");
            System.out.println("5. Borrar partida guardada");
            System.out.println("6. Salir");
            int choice = readInt("Selecciona opcion: ", 1, 6);

            switch (choice) {
                case 1 -> startNewGame();
                case 2 -> continueGame();
                case 3 -> importBook();
                case 4 -> printCatalog();
                case 5 -> clearSave();
                case 6 -> running = false;
                default -> {
                }
            }
        }

        System.out.println("Hasta luego.");
    }

    private void startNewGame() {
        List<BookAsset> books = catalog.listBooks();
        if (books.isEmpty()) {
            System.out.println("No hay libros. Importa uno primero.");
            return;
        }

        String player = readText("Nombre del jugador: ");
        BookAsset selectedBook = pickBook(books);
        if (selectedBook == null) {
            return;
        }

        GameSession session = new GameSession();
        session.setPlayerName(player);
        session.setBookPath(selectedBook.path().toString());
        session.setBookTitle(selectedBook.title());
        repository.save(session);

        play(session);
    }

    private void continueGame() {
        Optional<GameSession> maybeSession = repository.load();
        if (maybeSession.isEmpty()) {
            System.out.println("No existe una partida guardada.");
            return;
        }

        GameSession session = maybeSession.get();
        if (!Files.exists(Path.of(session.getBookPath()))) {
            System.out.println("No se encontro el libro de la partida: " + session.getBookPath());
            return;
        }
        play(session);
    }

    private void play(GameSession session) {
        Path path = Path.of(session.getBookPath());
        List<Scene> baseScenes = loader.loadScenes(path, config.sceneMaxChars(), config.sceneLinesPerChunk());
        List<NarrativeScene> scenes = narrativeBuilder.build(baseScenes);

        if (scenes.isEmpty()) {
            System.out.println("No fue posible crear narrativa jugable para este libro.");
            return;
        }

        while (!session.isCompleted() && session.getLife() > 0 && session.getCurrentScene() < scenes.size()) {
            NarrativeScene scene = scenes.get(session.getCurrentScene());
            printHud(session, scenes.size(), scene.title());
            System.out.println(scene.text());
            System.out.println();
            System.out.println("Evento: " + scene.eventType());
            System.out.println("Personaje: " + scene.npc());
            System.out.println();
            System.out.println("1. Dialogar");
            System.out.println("2. Explorar");
            System.out.println("3. Resolver reto");
            System.out.println("4. Usar item");
            System.out.println("5. Guardar y salir al menu");

            int choice = readInt("Decision: ", 1, 5);
            if (choice == 5) {
                repository.save(session);
                System.out.println("Partida guardada.");
                return;
            }

            TurnOutcome outcome;
            if (choice == 1) {
                outcome = gameEngine.apply(session, scene, PlayerAction.TALK, false, null);
            } else if (choice == 2) {
                outcome = gameEngine.apply(session, scene, PlayerAction.EXPLORE, false, null);
            } else if (choice == 3) {
                boolean correct = askChallenge(scene);
                outcome = gameEngine.apply(session, scene, PlayerAction.CHALLENGE, correct, null);
            } else {
                String itemId = chooseInventoryItem(session);
                outcome = gameEngine.apply(session, scene, PlayerAction.USE_ITEM, false, itemId);
            }

            System.out.println(outcome.message());
            if (outcome.consumedTurn()) {
                gameEngine.moveNextScene(session, scenes.size());
            }
            repository.save(session);

            if (session.getLife() <= 0) {
                session.setCompleted(true);
                repository.save(session);
                System.out.println("Te quedaste sin energia. Fin de partida.");
            }
        }

        if (session.getCurrentScene() >= scenes.size() && session.getLife() > 0) {
            session.setCompleted(true);
            repository.save(session);
            printFinalSummary(session);
        }
    }

    private boolean askChallenge(NarrativeScene scene) {
        var question = scene.challengeQuestion();
        System.out.println(question.prompt());
        for (int i = 0; i < question.options().size(); i++) {
            System.out.println((i + 1) + ". " + question.options().get(i));
        }
        int answer = readInt("Tu respuesta: ", 1, question.options().size());
        return question.isCorrect(answer);
    }

    private String chooseInventoryItem(GameSession session) {
        Map<String, Integer> inventory = session.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Inventario vacio.");
            return null;
        }

        List<String> itemIds = new ArrayList<>(inventory.keySet());
        for (int i = 0; i < itemIds.size(); i++) {
            String id = itemIds.get(i);
            InventoryItem item = gameEngine.itemCatalog().get(id);
            String name = item == null ? id : item.name();
            System.out.println((i + 1) + ". " + name + " x" + inventory.get(id));
        }
        int selected = readInt("Elige item: ", 1, itemIds.size());
        return itemIds.get(selected - 1);
    }

    private void printHud(GameSession session, int totalScenes, String sceneTitle) {
        int sceneNumber = session.getCurrentScene() + 1;
        int progress = (int) Math.floor((sceneNumber * 100.0) / Math.max(totalScenes, 1));
        System.out.println();
        System.out.println("Libro: " + session.getBookTitle());
        System.out.println(sceneTitle + " (" + sceneNumber + "/" + totalScenes + ", " + progress + "%)");
        System.out.println("Vida=" + session.getLife()
                + " | Conocimiento=" + session.getKnowledge()
                + " | Coraje=" + session.getCourage()
                + " | Enfoque=" + session.getFocus()
                + " | Puntaje=" + session.getScore());
        System.out.println("--------------------------------------------");
    }

    private void printFinalSummary(GameSession session) {
        System.out.println();
        System.out.println("Has completado la aventura de " + session.getBookTitle() + ".");
        System.out.println("Puntaje final: " + session.getScore());
        System.out.println("Respuestas correctas: " + session.getCorrectAnswers());
        System.out.println("Descubrimientos: " + session.getDiscoveries());
        System.out.println("Quests:");
        for (StoryQuest quest : session.evaluateQuests()) {
            String status = quest.completed() ? "COMPLETADA" : "PENDIENTE";
            System.out.println("- " + quest.title() + " -> " + status);
        }
    }

    private void importBook() {
        System.out.println("Ejemplo valido: file:///C:/Users/tu-usuario/Downloads/libro.pdf");
        String path = readText("Ruta local o file:/// : ");
        try {
            BookAsset asset = importer.importFromInput(path);
            System.out.println("Libro importado: " + asset.title() + " (" + asset.format() + ")");
        } catch (RuntimeException ex) {
            System.out.println("Error importando libro: " + ex.getMessage());
        }
    }

    private void printCatalog() {
        List<BookAsset> books = catalog.listBooks();
        if (books.isEmpty()) {
            System.out.println("Catalogo vacio.");
            return;
        }
        System.out.println("Catalogo:");
        for (int i = 0; i < books.size(); i++) {
            BookAsset asset = books.get(i);
            System.out.println((i + 1) + ". " + asset.title() + " [" + asset.format() + "]");
        }
    }

    private BookAsset pickBook(List<BookAsset> books) {
        System.out.println("Elige un libro:");
        for (int i = 0; i < books.size(); i++) {
            BookAsset asset = books.get(i);
            System.out.println((i + 1) + ". " + asset.title() + " [" + asset.format() + "]");
        }
        int option = readInt("Libro: ", 1, books.size());
        return books.get(option - 1);
    }

    private void clearSave() {
        repository.clear();
        System.out.println("Partida eliminada.");
    }

    private void ensureSamplesVisible() {
        copySampleIfMissing("books/caballero-union.txt", "caballero-union.txt");
        copySampleIfMissing("books/capitulo1.txt", "capitulo1.txt");
        catalog.listBooks();
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

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Entrada invalida. Usa un numero entre " + min + " y " + max + ".");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Entrada vacia.");
        }
    }
}
