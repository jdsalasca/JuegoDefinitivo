package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.app.AutoBookApplication;
import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.persistence.game.GameSessionRuntimeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AutoBookApplication.class)
class GameSessionRuntimeRepositoryTest {

    @Autowired
    private GameSessionRuntimeRepository repository;

    @Test
    void shouldPersistAndRestoreRuntimeSessionFromDatabase() {
        GameSession session = new GameSession();
        session.setPlayerName("Nina");
        session.setBookPath("C:/books/demo.txt");
        session.setBookTitle("demo.txt");
        session.setCurrentScene(3);
        session.setLife(88);
        session.setKnowledge(12);
        session.setCourage(9);
        session.setFocus(8);
        session.setScore(41);
        session.setCorrectAnswers(2);
        session.setDiscoveries(1);
        session.addInventoryItem("hint_star", 2);

        repository.save(
                "session-runtime-test",
                session,
                Map.of("Mentor", 4, "Bosque", 2),
                3,
                2,
                "Sesion guardada para restauracion."
        );

        GameSessionRuntimeRepository.StoredSession loaded = repository.load("session-runtime-test").orElseThrow();
        assertEquals("Nina", loaded.session().getPlayerName());
        assertEquals(3, loaded.session().getCurrentScene());
        assertEquals(88, loaded.session().getLife());
        assertEquals(41, loaded.session().getScore());
        assertEquals(3, loaded.challengeAttempts());
        assertEquals(2, loaded.challengeCorrect());
        assertEquals(4, loaded.narrativeMemory().get("Mentor"));
        assertTrue(loaded.session().getInventory().containsKey("hint_star"));
    }
}
