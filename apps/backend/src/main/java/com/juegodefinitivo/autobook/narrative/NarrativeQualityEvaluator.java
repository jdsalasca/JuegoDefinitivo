package com.juegodefinitivo.autobook.narrative;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NarrativeQualityEvaluator {

    public QualityReport evaluate(List<NarrativeScene> scenes) {
        if (scenes == null || scenes.isEmpty()) {
            return new QualityReport(0, 0, 0, 0);
        }

        int avgLength = (int) scenes.stream().mapToInt(scene -> scene.text().length()).average().orElse(0);
        double continuityCoverage = scenes.stream()
                .filter(scene -> scene.continuityHint() != null && !scene.continuityHint().isBlank())
                .count() / (double) scenes.size();

        Set<String> uniqueEntities = scenes.stream()
                .flatMap(scene -> scene.entities().stream())
                .collect(Collectors.toSet());

        long cognitiveLevels = scenes.stream()
                .map(NarrativeScene::cognitiveLevel)
                .filter(level -> level != null && !level.isBlank())
                .distinct()
                .count();

        int score = 0;
        if (avgLength >= 80 && avgLength <= 520) {
            score += 35;
        }
        if (continuityCoverage >= 0.8) {
            score += 30;
        }
        if (uniqueEntities.size() >= 3) {
            score += 20;
        }
        if (cognitiveLevels >= 2) {
            score += 15;
        }

        return new QualityReport(score, avgLength, uniqueEntities.size(), continuityCoverage);
    }

    public record QualityReport(int score, int averageSceneLength, int uniqueEntities, double continuityCoverage) {
    }
}

