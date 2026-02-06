package com.juegodefinitivo.autobook.engine;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.domain.TurnFeedback;

import java.util.Random;

public class AutoBookGameService {

    private final Random random;

    public AutoBookGameService(Random random) {
        this.random = random;
    }

    public GameSession newSession(String playerName, String bookPath, String bookTitle) {
        GameSession session = new GameSession();
        session.setPlayerName(playerName == null || playerName.isBlank() ? "Aventurero" : playerName.trim());
        session.setBookPath(bookPath);
        session.setBookTitle(bookTitle);
        return session;
    }

    public TurnFeedback applyChoice(GameSession session, int option, boolean answerCorrect) {
        return switch (option) {
            case 1 -> exploreCalmly(session);
            case 2 -> actBravely(session);
            case 3 -> studyScene(session, answerCorrect);
            default -> new TurnFeedback("Opcion invalida. No hubo cambios.", false);
        };
    }

    public void moveNextScene(GameSession session, int totalScenes) {
        session.advanceScene();
        if (session.getCurrentScene() >= totalScenes || session.getLife() <= 0) {
            session.setCompleted(true);
        }
    }

    private TurnFeedback exploreCalmly(GameSession session) {
        session.addFocus(2);
        session.addScore(5);
        return new TurnFeedback("Exploraste con calma y mejoraste tu enfoque.", false);
    }

    private TurnFeedback actBravely(GameSession session) {
        int damage = random.nextInt(13);
        session.addCourage(3);
        session.addScore(6);
        if (damage > 0) {
            session.addLife(-damage);
            return new TurnFeedback("Avanzaste con valentia, pero recibiste " + damage + " de dano.", true);
        }
        session.addScore(4);
        return new TurnFeedback("Tu valentia dio resultado y no recibiste dano.", false);
    }

    private TurnFeedback studyScene(GameSession session, boolean answerCorrect) {
        if (answerCorrect) {
            session.addKnowledge(4);
            session.addScore(10);
            return new TurnFeedback("Respuesta correcta. Tu conocimiento aumento.", false);
        }
        session.addLife(-5);
        session.addScore(2);
        return new TurnFeedback("Respuesta incorrecta. Aprendiste, pero perdiste energia.", true);
    }
}
