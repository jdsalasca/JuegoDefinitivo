package com.juegodefinitivo.autobook.narrative;

import com.juegodefinitivo.autobook.domain.ChallengeQuestion;
import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NarrativeBuilder {

    private static final Pattern KEYWORD_PATTERN = Pattern.compile("[\\p{L}]{5,}");
    private static final List<String> NPCS = List.of(
            "Maestra Alba", "Capitan Bruno", "Bibliotecaria Lira", "Cronista Sol", "Guia Orion"
    );

    private final AutoQuestionService questionService;
    private final Random random;

    public NarrativeBuilder(AutoQuestionService questionService, Random random) {
        this.questionService = questionService;
        this.random = random;
    }

    public List<NarrativeScene> build(List<Scene> baseScenes) {
        List<NarrativeScene> output = new ArrayList<>();
        for (int i = 0; i < baseScenes.size(); i++) {
            Scene scene = baseScenes.get(i);
            String keyword = pickKeyword(scene.text());
            SceneEventType eventType = chooseEventType(i);
            String npc = NPCS.get(i % NPCS.size());
            ChallengeQuestion question = questionService.createQuestion(scene);
            String discoveryItem = eventType == SceneEventType.DISCOVERY ? "relic_fragment" : "";

            output.add(new NarrativeScene(
                    i,
                    "Escena " + (i + 1) + ": " + capitalize(keyword),
                    scene.text(),
                    keyword,
                    npc,
                    eventType,
                    question,
                    discoveryItem
            ));
        }
        return output;
    }

    private SceneEventType chooseEventType(int index) {
        int pick = index % 5;
        return switch (pick) {
            case 0 -> SceneEventType.DIALOGUE;
            case 1 -> SceneEventType.CHALLENGE;
            case 2 -> SceneEventType.DISCOVERY;
            case 3 -> SceneEventType.BATTLE;
            default -> SceneEventType.REST;
        };
    }

    private String pickKeyword(String text) {
        List<String> words = new ArrayList<>();
        Matcher matcher = KEYWORD_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String word = matcher.group();
            if (!words.contains(word)) {
                words.add(word);
            }
        }
        if (words.isEmpty()) {
            return "historia";
        }
        return words.get(random.nextInt(words.size()));
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return "Historia";
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }
}
