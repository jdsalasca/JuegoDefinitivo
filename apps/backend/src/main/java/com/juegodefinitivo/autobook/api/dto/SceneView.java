package com.juegodefinitivo.autobook.api.dto;

import java.util.List;

public record SceneView(
        int index,
        int total,
        String title,
        String text,
        String eventType,
        String npc,
        ChallengeView challenge,
        List<String> entities,
        String cognitiveLevel,
        String continuityHint
) {
}
