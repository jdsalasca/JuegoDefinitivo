package com.juegodefinitivo.autobook.narrative;

import com.juegodefinitivo.autobook.domain.ChallengeQuestion;

public record NarrativeScene(
        int index,
        String title,
        String text,
        String keyword,
        String npc,
        SceneEventType eventType,
        ChallengeQuestion challengeQuestion,
        String discoveryItemId
) {
}
