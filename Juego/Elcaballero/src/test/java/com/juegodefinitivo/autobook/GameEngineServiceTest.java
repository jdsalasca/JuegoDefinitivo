package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.engine.PlayerAction;
import com.juegodefinitivo.autobook.narrative.DialogueService;
import com.juegodefinitivo.autobook.narrative.NarrativeScene;
import com.juegodefinitivo.autobook.narrative.SceneEventType;
import com.juegodefinitivo.autobook.domain.ChallengeQuestion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineServiceTest {

    @Test
    void shouldUsePotionWithoutConsumingTurn() {
        GameEngineService engine = new GameEngineService(new DialogueService(), new Random(10));
        GameSession session = new GameSession();
        session.setLife(50);
        session.addInventoryItem("potion_small", 1);

        NarrativeScene scene = new NarrativeScene(
                0,
                "Escena",
                "Texto",
                "texto",
                "NPC",
                SceneEventType.DIALOGUE,
                new ChallengeQuestion("q", List.of("a", "b", "c"), 1),
                ""
        );

        var outcome = engine.apply(session, scene, PlayerAction.USE_ITEM, false, "potion_small");

        assertTrue(outcome.message().contains("Pocion"));
        assertTrue(!outcome.consumedTurn());
        assertEquals(65, session.getLife());
    }
}
