package com.juegodefinitivo.autobook.persistence;

import com.juegodefinitivo.autobook.domain.GameSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class SaveGameRepository {

    private final Path saveFile;

    public SaveGameRepository(Path saveFile) {
        this.saveFile = saveFile;
    }

    public void save(GameSession session) {
        try {
            Files.createDirectories(saveFile.getParent());
            Properties properties = new Properties();
            properties.setProperty("playerName", session.getPlayerName());
            properties.setProperty("bookPath", session.getBookPath());
            properties.setProperty("bookTitle", session.getBookTitle());
            properties.setProperty("currentScene", String.valueOf(session.getCurrentScene()));
            properties.setProperty("life", String.valueOf(session.getLife()));
            properties.setProperty("knowledge", String.valueOf(session.getKnowledge()));
            properties.setProperty("courage", String.valueOf(session.getCourage()));
            properties.setProperty("focus", String.valueOf(session.getFocus()));
            properties.setProperty("score", String.valueOf(session.getScore()));
            properties.setProperty("correctAnswers", String.valueOf(session.getCorrectAnswers()));
            properties.setProperty("discoveries", String.valueOf(session.getDiscoveries()));
            properties.setProperty("completed", String.valueOf(session.isCompleted()));
            properties.setProperty("inventory", encodeInventory(session.getInventory()));

            try (OutputStream output = Files.newOutputStream(saveFile)) {
                properties.store(output, "AutoBook savegame");
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la partida", e);
        }
    }

    public Optional<GameSession> load() {
        if (!Files.exists(saveFile)) {
            return Optional.empty();
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(saveFile)) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la partida guardada", e);
        }

        GameSession session = new GameSession();
        session.setPlayerName(properties.getProperty("playerName", "Aventurero"));
        session.setBookPath(properties.getProperty("bookPath", ""));
        session.setBookTitle(properties.getProperty("bookTitle", ""));
        session.setCurrentScene(parseInt(properties, "currentScene", 0));
        session.setLife(parseInt(properties, "life", 100));
        session.setKnowledge(parseInt(properties, "knowledge", 0));
        session.setCourage(parseInt(properties, "courage", 0));
        session.setFocus(parseInt(properties, "focus", 0));
        session.setScore(parseInt(properties, "score", 0));
        session.setCorrectAnswers(parseInt(properties, "correctAnswers", 0));
        session.setDiscoveries(parseInt(properties, "discoveries", 0));
        session.setCompleted(Boolean.parseBoolean(properties.getProperty("completed", "false")));
        session.replaceInventory(decodeInventory(properties.getProperty("inventory", "")));

        return Optional.of(session);
    }

    public void clear() {
        try {
            Files.deleteIfExists(saveFile);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo eliminar la partida guardada", e);
        }
    }

    private int parseInt(Properties properties, String key, int fallback) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private String encodeInventory(Map<String, Integer> inventory) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(entry.getKey()).append(':').append(entry.getValue());
        }
        return builder.toString();
    }

    private Map<String, Integer> decodeInventory(String value) {
        Map<String, Integer> inventory = new LinkedHashMap<>();
        if (value == null || value.isBlank()) {
            return inventory;
        }

        String[] tokens = value.split(";");
        for (String token : tokens) {
            if (token.isBlank() || !token.contains(":")) {
                continue;
            }
            String[] pair = token.split(":", 2);
            if (pair.length != 2) {
                continue;
            }
            try {
                int qty = Integer.parseInt(pair[1]);
                if (qty > 0) {
                    inventory.put(pair[0], qty);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return inventory;
    }
}
