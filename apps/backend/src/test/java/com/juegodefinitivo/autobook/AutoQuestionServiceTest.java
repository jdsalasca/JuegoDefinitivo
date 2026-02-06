package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.ChallengeQuestion;
import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoQuestionServiceTest {

    @Test
    void shouldGenerateThreeOptionsAndValidCorrectAnswer() {
        AutoQuestionService service = new AutoQuestionService(new Random(7));
        Scene scene = new Scene(0, "El dragon protege la biblioteca antigua del reino.");

        ChallengeQuestion question = service.createQuestion(scene);

        assertEquals(3, question.options().size());
        assertTrue(question.correctOptionIndex() >= 1 && question.correctOptionIndex() <= 3);
        assertTrue(question.options().stream().anyMatch(o -> o.equals("dragon") || o.equals("protege") || o.equals("biblioteca") || o.equals("antigua") || o.equals("reino")));
    }
}
