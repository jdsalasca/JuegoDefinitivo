package com.juegodefinitivo.autobook.narrative;

import com.juegodefinitivo.autobook.domain.ChallengeQuestion;

import java.util.List;

public record NarrativeScene(
        int index,
        String title,
        String text,
        String keyword,
        String npc,
        SceneEventType eventType,
        ChallengeQuestion challengeQuestion,
        String discoveryItemId,
        List<String> entities,
        String cognitiveLevel,
        String continuityHint
) {
}
