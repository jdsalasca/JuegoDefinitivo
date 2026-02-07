package com.juegodefinitivo.autobook.api.dto;

import java.util.List;
import java.util.Map;

public record GameStateResponse(
        String sessionId,
        String playerName,
        String bookTitle,
        boolean completed,
        int life,
        int knowledge,
        int courage,
        int focus,
        int score,
        int correctAnswers,
        int discoveries,
        Map<String, Integer> inventory,
        Map<String, Integer> narrativeMemory,
        String adaptiveDifficulty,
        List<QuestView> quests,
        SceneView currentScene,
        String lastMessage
) {
}
