package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.engine.AutoBookGameService;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoBookGameServiceTest {

    @Test
    void shouldApplyStudyChoiceWhenAnswerIsCorrect() {
        AutoBookGameService service = new AutoBookGameService(new Random(1));
        GameSession session = service.newSession("Ana", "book.txt", "book.txt");

        service.applyChoice(session, 3, true);

        assertEquals(4, session.getKnowledge());
        assertEquals(10, session.getScore());
    }

    @Test
    void shouldMoveToCompletedWhenScenesEnd() {
        AutoBookGameService service = new AutoBookGameService(new Random(1));
        GameSession session = service.newSession("Ana", "book.txt", "book.txt");

        service.moveNextScene(session, 1);

        assertTrue(session.isCompleted());
        assertEquals(1, session.getCurrentScene());
    }
}
