package com.juegodefinitivo.autobook.engine;

import com.juegodefinitivo.autobook.domain.ChallengeQuestion;
import com.juegodefinitivo.autobook.domain.Scene;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoQuestionService {

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]{4,}");
    private static final List<String> DISTRACTORS = List.of(
            "aventura", "bosque", "estrella", "puente", "camino", "memoria", "palabra", "mensaje", "valentia", "aprendizaje"
    );

    private final Random random;

    public AutoQuestionService(Random random) {
        this.random = random;
    }

    public ChallengeQuestion createQuestion(Scene scene) {
        return createQuestion(scene, "LITERAL");
    }

    public ChallengeQuestion createQuestion(Scene scene, String cognitiveLevel) {
        List<String> keywords = extractKeywords(scene.text());
        String correct = keywords.isEmpty() ? "historia" : keywords.get(random.nextInt(keywords.size()));

        Set<String> options = new LinkedHashSet<>();
        options.add(correct);
        int i = 0;
        while (options.size() < 3 && i < DISTRACTORS.size()) {
            String candidate = DISTRACTORS.get(i++);
            if (!normalize(candidate).equals(normalize(correct))) {
                options.add(candidate);
            }
        }
        while (options.size() < 3) {
            options.add("texto");
        }

        List<String> optionList = new ArrayList<>(options);
        java.util.Collections.shuffle(optionList, random);
        int correctIndex = optionList.indexOf(correct) + 1;

        return new ChallengeQuestion(promptFor(cognitiveLevel), optionList, correctIndex);
    }

    private String promptFor(String cognitiveLevel) {
        if (cognitiveLevel == null) {
            return "Que palabra aparece en la escena?";
        }
        return switch (cognitiveLevel.toUpperCase(Locale.ROOT)) {
            case "INFERENCIAL" -> "Que concepto resume mejor lo que ocurre en la escena?";
            case "CRITICO" -> "Que opcion representa mejor la decision mas sabia en esta escena?";
            default -> "Que palabra aparece en la escena?";
        };
    }

    private List<String> extractKeywords(String text) {
        List<String> words = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String word = matcher.group();
            if (!words.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    private String normalize(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }
}
