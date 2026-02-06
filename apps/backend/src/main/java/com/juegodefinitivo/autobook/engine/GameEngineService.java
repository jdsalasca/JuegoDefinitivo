package com.juegodefinitivo.autobook.engine;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.domain.InventoryItem;
import com.juegodefinitivo.autobook.domain.ItemType;
import com.juegodefinitivo.autobook.narrative.DialogueService;
import com.juegodefinitivo.autobook.narrative.NarrativeScene;
import com.juegodefinitivo.autobook.narrative.SceneEventType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class GameEngineService {

    private final DialogueService dialogueService;
    private final Random random;
    private final Map<String, InventoryItem> itemCatalog;

    public GameEngineService(DialogueService dialogueService, Random random) {
        this.dialogueService = dialogueService;
        this.random = random;
        this.itemCatalog = buildCatalog();
    }

    public TurnOutcome apply(GameSession session, NarrativeScene scene, PlayerAction action, boolean challengeCorrect, String itemId) {
        return switch (action) {
            case TALK -> handleTalk(session, scene);
            case EXPLORE -> handleExplore(session, scene);
            case CHALLENGE -> handleChallenge(session, scene, challengeCorrect);
            case USE_ITEM -> handleUseItem(session, itemId);
        };
    }

    public void moveNextScene(GameSession session, int totalScenes) {
        session.advanceScene();
        if (session.getCurrentScene() >= totalScenes || session.getLife() <= 0) {
            session.setCompleted(true);
        }
    }

    public Map<String, InventoryItem> itemCatalog() {
        return itemCatalog;
    }

    private TurnOutcome handleTalk(GameSession session, NarrativeScene scene) {
        String dialogue = dialogueService.buildDialogue(scene);
        session.addFocus(1);
        session.addKnowledge(1);
        session.addScore(4);
        if (scene.eventType() == SceneEventType.REST) {
            session.addLife(6);
            return new TurnOutcome(dialogue + " Recuperaste energia en el descanso.", true);
        }
        return new TurnOutcome(dialogue, true);
    }

    private TurnOutcome handleExplore(GameSession session, NarrativeScene scene) {
        session.addFocus(2);
        session.addScore(5);

        if (scene.eventType() == SceneEventType.DISCOVERY) {
            session.addDiscovery();
            session.addInventoryItem("relic_fragment", 1);
            return new TurnOutcome("Exploraste y encontraste un Fragmento de Relicario.", true);
        }

        if (scene.eventType() == SceneEventType.BATTLE) {
            int damage = 6 + random.nextInt(10);
            session.addLife(-damage);
            session.addCourage(2);
            return new TurnOutcome("Exploraste una zona peligrosa y recibiste " + damage + " de dano.", true);
        }

        if (random.nextInt(100) < 30) {
            session.addInventoryItem("potion_small", 1);
            return new TurnOutcome("Exploraste con exito y encontraste una Pocion pequena.", true);
        }

        return new TurnOutcome("Exploraste la escena y observaste detalles utiles para avanzar.", true);
    }

    private TurnOutcome handleChallenge(GameSession session, NarrativeScene scene, boolean challengeCorrect) {
        if (challengeCorrect) {
            session.addCorrectAnswer();
            session.addKnowledge(4);
            session.addScore(12);
            if (scene.eventType() == SceneEventType.CHALLENGE) {
                session.addInventoryItem("ink_token", 1);
                return new TurnOutcome("Reto superado. Recibiste una Ficha de Tinta.", true);
            }
            return new TurnOutcome("Reto superado. Comprendiste mejor la lectura.", true);
        }

        int damage = scene.eventType() == SceneEventType.BATTLE ? 10 : 5;
        session.addLife(-damage);
        session.addScore(2);
        return new TurnOutcome("Reto fallido. Perdiste " + damage + " de energia.", true);
    }

    private TurnOutcome handleUseItem(GameSession session, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return new TurnOutcome("No seleccionaste item.", false);
        }
        InventoryItem item = itemCatalog.get(itemId);
        if (item == null) {
            return new TurnOutcome("Item desconocido.", false);
        }
        if (!session.removeInventoryItem(itemId, 1)) {
            return new TurnOutcome("No tienes ese item en inventario.", false);
        }

        if (item.type() == ItemType.POTION) {
            session.addLife(item.power());
            session.addScore(3);
            return new TurnOutcome("Usaste " + item.name() + " y recuperaste energia.", false);
        }
        if (item.type() == ItemType.TOOL) {
            session.addFocus(item.power());
            session.addKnowledge(1);
            return new TurnOutcome("Usaste " + item.name() + " y aumentaste enfoque.", false);
        }

        session.addCourage(1);
        session.addScore(1);
        return new TurnOutcome("Inspeccionaste " + item.name() + ". Te dio motivacion.", false);
    }

    private Map<String, InventoryItem> buildCatalog() {
        Map<String, InventoryItem> items = new LinkedHashMap<>();
        items.put("potion_small", new InventoryItem(
                "potion_small",
                "Pocion pequena",
                "Recupera energia basica.",
                ItemType.POTION,
                15
        ));
        items.put("relic_fragment", new InventoryItem(
                "relic_fragment",
                "Fragmento de Relicario",
                "Objeto de coleccion de la historia.",
                ItemType.RELIC,
                0
        ));
        items.put("ink_token", new InventoryItem(
                "ink_token",
                "Ficha de Tinta",
                "Ayuda a enfocar el aprendizaje.",
                ItemType.TOOL,
                2
        ));
        return items;
    }
}
