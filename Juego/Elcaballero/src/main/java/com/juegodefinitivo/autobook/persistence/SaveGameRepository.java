package com.juegodefinitivo.autobook.persistence;

import com.juegodefinitivo.autobook.domain.GameSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
            properties.setProperty("completed", String.valueOf(session.isCompleted()));

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
        session.setCurrentScene(Integer.parseInt(properties.getProperty("currentScene", "0")));
        session.setLife(Integer.parseInt(properties.getProperty("life", "100")));
        session.setKnowledge(Integer.parseInt(properties.getProperty("knowledge", "0")));
        session.setCourage(Integer.parseInt(properties.getProperty("courage", "0")));
        session.setFocus(Integer.parseInt(properties.getProperty("focus", "0")));
        session.setScore(Integer.parseInt(properties.getProperty("score", "0")));
        session.setCompleted(Boolean.parseBoolean(properties.getProperty("completed", "false")));

        return Optional.of(session);
    }

    public void clear() {
        try {
            Files.deleteIfExists(saveFile);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo eliminar la partida guardada", e);
        }
    }
}
