package com.juegodefinitivo.autobook.api.dto;

public record SceneView(
        int index,
        int total,
        String title,
        String text,
        String eventType,
        String npc,
        ChallengeView challenge
) {
}
