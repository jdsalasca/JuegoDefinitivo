package com.juegodefinitivo.autobook.ui;

import com.juegodefinitivo.autobook.config.AppConfig;
import com.juegodefinitivo.autobook.domain.ChallengeQuestion;
import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.domain.TurnFeedback;
import com.juegodefinitivo.autobook.engine.AutoBookGameService;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.engine.BookParser;
import com.juegodefinitivo.autobook.persistence.SaveGameRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleGameUI {

    private final AppConfig config;
    private final SaveGameRepository repository;
    private final BookParser parser;
    private final AutoBookGameService gameService;
    private final AutoQuestionService questionService;
    private final Scanner scanner;

    public ConsoleGameUI(
            AppConfig config,
            SaveGameRepository repository,
            BookParser parser,
            AutoBookGameService gameService,
            AutoQuestionService questionService,
            Scanner scanner
    ) {
        this.config = config;
        this.repository = repository;
        this.parser = parser;
        this.gameService = gameService;
        this.questionService = questionService;
        this.scanner = scanner;
    }

    public void run() {
        ensureBookCatalog();

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== " + config.appName() + " =====");
            System.out.println("1. Nueva partida");
            System.out.println("2. Continuar partida");
            System.out.println("3. Borrar partida guardada");
            System.out.println("4. Salir");
            int choice = readInt("Selecciona una opcion: ", 1, 4);

            switch (choice) {
                case 1 -> startNewGame();
                case 2 -> continueGame();
                case 3 -> deleteSave();
                case 4 -> running = false;
                default -> {
                }
            }
        }

        System.out.println("Hasta luego.");
    }

    private void startNewGame() {
        String playerName = readText("Nombre del jugador: ");
        Path bookPath = chooseBookPath();
        if (bookPath == null) {
            return;
        }

        List<Scene> scenes = parser.parse(bookPath, config.sceneMaxChars(), config.sceneLinesPerChunk());
        if (scenes.isEmpty()) {
            System.out.println("No se pudieron crear escenas para ese libro.");
            return;
        }

        GameSession session = gameService.newSession(playerName, bookPath.toString(), bookPath.getFileName().toString());
        repository.save(session);
        play(session, scenes);
    }

    private void continueGame() {
        Optional<GameSession> maybeSession = repository.load();
        if (maybeSession.isEmpty()) {
            System.out.println("No hay partida guardada.");
            return;
        }

        GameSession session = maybeSession.get();
        Path bookPath = Path.of(session.getBookPath());
        if (!Files.exists(bookPath)) {
            System.out.println("El libro guardado ya no existe: " + bookPath);
            return;
        }

        List<Scene> scenes = parser.parse(bookPath, config.sceneMaxChars(), config.sceneLinesPerChunk());
        if (session.getCurrentScene() >= scenes.size()) {
            System.out.println("La partida ya estaba finalizada.");
            return;
        }

        play(session, scenes);
    }

    private void play(GameSession session, List<Scene> scenes) {
        while (!session.isCompleted() && session.getLife() > 0 && session.getCurrentScene() < scenes.size()) {
            Scene scene = scenes.get(session.getCurrentScene());
            printHeader(session, scenes.size());
            System.out.println(scene.text());
            System.out.println();
            System.out.println("1. Explorar con calma (+enfoque, +puntos)");
            System.out.println("2. Avanzar con valentia (riesgo, +coraje)");
            System.out.println("3. Estudiar escena (pregunta educativa)");
            System.out.println("4. Guardar y salir al menu");

            int choice = readInt("Tu decision: ", 1, 4);
            if (choice == 4) {
                repository.save(session);
                System.out.println("Partida guardada.");
                return;
            }

            boolean answerCorrect = false;
            if (choice == 3) {
                ChallengeQuestion question = questionService.createQuestion(scene);
                System.out.println(question.prompt());
                for (int i = 0; i < question.options().size(); i++) {
                    System.out.println((i + 1) + ". " + question.options().get(i));
                }
                int answer = readInt("Respuesta: ", 1, question.options().size());
                answerCorrect = question.isCorrect(answer);
            }

            TurnFeedback feedback = gameService.applyChoice(session, choice, answerCorrect);
            gameService.moveNextScene(session, scenes.size());
            repository.save(session);
            System.out.println(feedback.message());

            if (session.getLife() <= 0) {
                System.out.println("Te quedaste sin energia. Fin de partida.");
            }
        }

        if (session.getCurrentScene() >= scenes.size() && session.getLife() > 0) {
            System.out.println("Completaste el libro. Puntaje final: " + session.getScore());
            session.setCompleted(true);
            repository.save(session);
        }
    }

    private void deleteSave() {
        repository.clear();
        System.out.println("Partida guardada eliminada.");
    }

    private void printHeader(GameSession session, int totalScenes) {
        int sceneNumber = session.getCurrentScene() + 1;
        int progress = (int) Math.floor((sceneNumber * 100.0) / Math.max(1, totalScenes));
        System.out.println();
        System.out.println("Libro: " + session.getBookTitle());
        System.out.println("Escena " + sceneNumber + "/" + totalScenes + " (" + progress + "%)");
        System.out.println("Vida=" + session.getLife() + " | Conocimiento=" + session.getKnowledge() + " | Coraje=" + session.getCourage() + " | Enfoque=" + session.getFocus() + " | Puntaje=" + session.getScore());
        System.out.println("----------------------------------------");
    }

    private Path chooseBookPath() {
        List<Path> books = listBooks();
        if (!books.isEmpty()) {
            System.out.println("Libros detectados:");
            for (int i = 0; i < books.size(); i++) {
                System.out.println((i + 1) + ". " + books.get(i).getFileName());
            }
        } else {
            System.out.println("No hay libros en " + config.booksDir());
        }

        System.out.println("0. Escribir ruta manual");
        int min = books.isEmpty() ? 0 : 0;
        int max = books.size();
        int selected = readInt("Selecciona libro: ", min, max);
        if (selected == 0) {
            String manualPath = readText("Ruta del archivo .txt: ");
            Path path = Path.of(manualPath).toAbsolutePath().normalize();
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                System.out.println("Ruta invalida.");
                return null;
            }
            return path;
        }

        if (selected < 1 || selected > books.size()) {
            System.out.println("Opcion invalida.");
            return null;
        }
        return books.get(selected - 1);
    }

    private List<Path> listBooks() {
        List<Path> books = new ArrayList<>();
        try {
            Files.createDirectories(config.booksDir());
            try (var stream = Files.list(config.booksDir())) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                        .sorted()
                        .forEach(books::add);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el catalogo de libros", e);
        }
        return books;
    }

    private void ensureBookCatalog() {
        try {
            Files.createDirectories(config.booksDir());
            copySampleIfMissing("books/caballero-union.txt", "caballero-union.txt");
            copySampleIfMissing("books/capitulo1.txt", "capitulo1.txt");
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo preparar el catalogo de libros", e);
        }
    }

    private void copySampleIfMissing(String resourcePath, String targetName) throws IOException {
        Path target = config.booksDir().resolve(targetName);
        if (Files.exists(target)) {
            return;
        }
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                return;
            }
            Files.copy(resource, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int parsed = Integer.parseInt(value);
                if (parsed >= min && parsed <= max) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Entrada invalida. Debe ser un numero entre " + min + " y " + max + ".");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("El texto no puede estar vacio.");
        }
    }
}
