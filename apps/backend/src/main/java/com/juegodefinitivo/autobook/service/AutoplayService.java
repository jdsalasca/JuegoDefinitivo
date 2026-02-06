package com.juegodefinitivo.autobook.service;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.engine.PlayerAction;
import com.juegodefinitivo.autobook.narrative.NarrativeScene;
import com.juegodefinitivo.autobook.narrative.SceneEventType;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Random;

@Service
public class AutoplayService {

    private final Random random;

    public AutoplayService(Random random) {
        this.random = random;
    }

    public PlannedTurn planTurn(GameSession session, NarrativeScene scene, String ageBandInput, String readingLevelInput) {
        AgeBand ageBand = AgeBand.from(ageBandInput);
        ReadingLevel readingLevel = ReadingLevel.from(readingLevelInput);

        if (session.getLife() <= 40 && session.getInventory().getOrDefault("potion_small", 0) > 0) {
            return new PlannedTurn(PlayerAction.USE_ITEM, false, "potion_small", "Auto: usa pocion para estabilizar vida.");
        }

        PlayerAction action = switch (scene.eventType()) {
            case REST -> PlayerAction.TALK;
            case DISCOVERY -> PlayerAction.EXPLORE;
            case CHALLENGE -> PlayerAction.CHALLENGE;
            case DIALOGUE -> readingLevel == ReadingLevel.ADVANCED ? PlayerAction.EXPLORE : PlayerAction.TALK;
            case BATTLE -> pickBattleAction(ageBand, readingLevel, session);
        };

        boolean challengeCorrect = false;
        if (action == PlayerAction.CHALLENGE) {
            challengeCorrect = shouldAnswerCorrect(ageBand, readingLevel);
        }

        return new PlannedTurn(action, challengeCorrect, null, "Auto: " + action.name() + " en evento " + scene.eventType().name() + ".");
    }

    private PlayerAction pickBattleAction(AgeBand ageBand, ReadingLevel readingLevel, GameSession session) {
        if (ageBand == AgeBand.EARLY || readingLevel == ReadingLevel.BEGINNER) {
            if (session.getInventory().getOrDefault("potion_small", 0) > 0 && session.getLife() < 55) {
                return PlayerAction.USE_ITEM;
            }
            return PlayerAction.TALK;
        }
        return random.nextInt(100) < 60 ? PlayerAction.CHALLENGE : PlayerAction.EXPLORE;
    }

    private boolean shouldAnswerCorrect(AgeBand ageBand, ReadingLevel readingLevel) {
        int accuracy = switch (readingLevel) {
            case BEGINNER -> 55;
            case INTERMEDIATE -> 75;
            case ADVANCED -> 90;
        };
        if (ageBand == AgeBand.EARLY) {
            accuracy -= 10;
        }
        if (ageBand == AgeBand.TEEN) {
            accuracy += 5;
        }
        int roll = random.nextInt(100);
        return roll < Math.max(20, Math.min(95, accuracy));
    }

    private enum AgeBand {
        EARLY,
        MIDDLE,
        TEEN;

        static AgeBand from(String value) {
            if (value == null || value.isBlank()) {
                return MIDDLE;
            }
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if (normalized.contains("6") || normalized.contains("7") || normalized.contains("8") || normalized.contains("early")) {
                return EARLY;
            }
            if (normalized.contains("13") || normalized.contains("teen")) {
                return TEEN;
            }
            return MIDDLE;
        }
    }

    private enum ReadingLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED;

        static ReadingLevel from(String value) {
            if (value == null || value.isBlank()) {
                return INTERMEDIATE;
            }
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if (normalized.startsWith("beg")) {
                return BEGINNER;
            }
            if (normalized.startsWith("adv")) {
                return ADVANCED;
            }
            return INTERMEDIATE;
        }
    }

    public record PlannedTurn(PlayerAction action, boolean challengeCorrect, String itemId, String rationale) {
    }
}

