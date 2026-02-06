package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.persistence.SaveGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveGameRepositoryTest {

    @Test
    void shouldPersistAndRestoreSession(@TempDir Path tempDir) {
        Path savePath = tempDir.resolve("save.properties");
        SaveGameRepository repository = new SaveGameRepository(savePath);

        GameSession session = new GameSession();
        session.setPlayerName("Juan");
        session.setBookPath("C:/book.txt");
        session.setBookTitle("book.txt");
        session.setCurrentScene(4);
        session.setLife(87);
        session.setKnowledge(9);
        session.setCourage(7);
        session.setFocus(5);
        session.setScore(33);

        repository.save(session);
        GameSession loaded = repository.load().orElseThrow();

        assertEquals("Juan", loaded.getPlayerName());
        assertEquals(4, loaded.getCurrentScene());
        assertEquals(87, loaded.getLife());
        assertEquals(33, loaded.getScore());
        assertTrue(repository.load().isPresent());
    }
}
