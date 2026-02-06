package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.api.dto.GameStateResponse;
import com.juegodefinitivo.autobook.app.AutoBookApplication;
import com.juegodefinitivo.autobook.service.GameFacadeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AutoBookApplication.class)
class GameFacadeServiceTest {

    @Autowired
    private GameFacadeService facade;

    @Test
    void shouldStartGameAndAdvanceWithTalk() {
        facade.bootstrapSamples();
        var books = facade.listBooks();
        assertTrue(!books.isEmpty());

        GameStateResponse state = facade.startGame("Ana", books.get(0).path());
        assertNotNull(state.sessionId());
        assertNotNull(state.currentScene());

        int initialScene = state.currentScene().index();
        GameStateResponse updated = facade.applyAction(state.sessionId(), "TALK", null, null);

        if (updated.currentScene() != null) {
            assertTrue(updated.currentScene().index() >= initialScene);
        }
        assertTrue(updated.score() >= state.score());
    }

    @Test
    void shouldRejectChallengeWithoutAnswer() {
        facade.bootstrapSamples();
        var books = facade.listBooks();
        GameStateResponse state = facade.startGame("Luis", books.get(0).path());

        assertThrows(IllegalArgumentException.class,
                () -> facade.applyAction(state.sessionId(), "CHALLENGE", null, null));
    }

    @Test
    void shouldRejectUnknownSession() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.getState("missing-session"));
        assertEquals("Sesion no encontrada.", ex.getMessage());
    }

    @Test
    void shouldAdvanceGameWithAutoplay() {
        facade.bootstrapSamples();
        var books = facade.listBooks();
        GameStateResponse state = facade.startGame("Sofia", books.get(0).path());

        GameStateResponse updated = facade.applyAutoplay(state.sessionId(), "9-12", "intermediate", 4);

        assertTrue(updated.score() >= state.score());
        assertNotNull(updated.lastMessage());
        assertTrue(updated.lastMessage().contains("auto-steps"));
    }
}
