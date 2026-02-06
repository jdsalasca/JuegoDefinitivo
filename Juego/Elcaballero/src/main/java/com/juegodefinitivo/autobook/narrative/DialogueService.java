package com.juegodefinitivo.autobook.narrative;

public class DialogueService {

    public String buildDialogue(NarrativeScene scene) {
        return scene.npc() + ": Si entiendes la idea de '" + scene.keyword()
                + "', podras avanzar por este capitulo con confianza.";
    }
}
