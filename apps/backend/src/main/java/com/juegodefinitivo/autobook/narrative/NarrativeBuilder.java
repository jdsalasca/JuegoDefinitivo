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
    private final EntityExtractor entityExtractor;
    private final Random random;

    public NarrativeBuilder(AutoQuestionService questionService, EntityExtractor entityExtractor, Random random) {
        this.questionService = questionService;
        this.entityExtractor = entityExtractor;
        this.random = random;
    }

    public List<NarrativeScene> build(List<Scene> baseScenes) {
        List<NarrativeScene> output = new ArrayList<>();
        SceneEventType previousEvent = SceneEventType.DIALOGUE;
        String previousNpc = NPCS.get(0);
        for (int i = 0; i < baseScenes.size(); i++) {
            Scene scene = baseScenes.get(i);
            String keyword = pickKeyword(scene.text());
            List<String> entities = entityExtractor.extractEntities(scene.text());
            String cognitiveLevel = cognitiveLevelFor(i);
            SceneEventType eventType = chooseEventType(i, previousEvent, scene.text());
            String npc = chooseNpc(i, entities, previousNpc);
            ChallengeQuestion question = questionService.createQuestion(scene, cognitiveLevel);
            String discoveryItem = eventType == SceneEventType.DISCOVERY ? "relic_fragment" : "";
            String continuityHint = continuityHint(previousEvent, eventType, entities);

            output.add(new NarrativeScene(
                    i,
                    "Escena " + (i + 1) + ": " + capitalize(keyword),
                    scene.text(),
                    keyword,
                    npc,
                    eventType,
                    question,
                    discoveryItem,
                    entities,
                    cognitiveLevel,
                    continuityHint
            ));
            previousEvent = eventType;
            previousNpc = npc;
        }
        return output;
    }

    private SceneEventType chooseEventType(int index, SceneEventType previous, String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("pregunta") || lower.contains("reto")) {
            return SceneEventType.CHALLENGE;
        }
        if (lower.contains("descubr") || lower.contains("encuentra")) {
            return SceneEventType.DISCOVERY;
        }
        if (lower.contains("descans") || lower.contains("calma")) {
            return SceneEventType.REST;
        }

        int pick = index % 5;
        SceneEventType candidate = switch (pick) {
            case 0 -> SceneEventType.DIALOGUE;
            case 1 -> SceneEventType.CHALLENGE;
            case 2 -> SceneEventType.DISCOVERY;
            case 3 -> SceneEventType.BATTLE;
            default -> SceneEventType.REST;
        };
        if (previous == SceneEventType.BATTLE && candidate == SceneEventType.BATTLE) {
            return SceneEventType.REST;
        }
        return candidate;
    }

    private String chooseNpc(int index, List<String> entities, String previousNpc) {
        if (!entities.isEmpty() && entities.get(0).startsWith("Lugar:")) {
            return "Guia Orion";
        }
        if (index > 0 && random.nextInt(100) < 35) {
            return previousNpc;
        }
        return NPCS.get(index % NPCS.size());
    }

    private String cognitiveLevelFor(int index) {
        int bucket = index % 3;
        return switch (bucket) {
            case 1 -> "INFERENCIAL";
            case 2 -> "CRITICO";
            default -> "LITERAL";
        };
    }

    private String continuityHint(SceneEventType previous, SceneEventType current, List<String> entities) {
        String anchor = entities.isEmpty() ? "contexto narrativo" : entities.get(0);
        if (previous == SceneEventType.BATTLE && current == SceneEventType.REST) {
            return "Transicion de tension a recuperacion, manteniendo el foco en " + anchor + ".";
        }
        if (current == SceneEventType.DISCOVERY) {
            return "Nueva pista conectada a " + anchor + ".";
        }
        return "Mantener continuidad con " + anchor + ".";
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
