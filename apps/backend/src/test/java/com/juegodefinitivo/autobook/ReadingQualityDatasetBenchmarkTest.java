package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.narrative.EntityExtractor;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import com.juegodefinitivo.autobook.narrative.NarrativeQualityEvaluator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadingQualityDatasetBenchmarkTest {

    @Test
    void shouldKeepAverageNarrativeScoreAboveThresholdAcrossDataset() throws IOException {
        String raw = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("benchmarks/reading-samples.txt"),
                        "benchmark dataset not found"
                ).readAllBytes(),
                StandardCharsets.UTF_8
        );

        String[] books = raw.split("---");
        NarrativeBuilder builder = new NarrativeBuilder(new AutoQuestionService(new Random(13)), new EntityExtractor(), new Random(13));
        NarrativeQualityEvaluator evaluator = new NarrativeQualityEvaluator();

        List<Integer> scores = new ArrayList<>();
        for (String book : books) {
            String text = book.trim();
            if (text.isBlank()) {
                continue;
            }
            List<Scene> scenes = parseScenes(text);
            int score = evaluator.evaluate(builder.build(scenes)).score();
            scores.add(score);
        }

        double average = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        assertTrue(average >= 65.0, "Average quality score should stay above minimum threshold");
    }

    private List<Scene> parseScenes(String text) {
        String[] lines = text.split("\n");
        List<Scene> scenes = new ArrayList<>();
        int idx = 0;
        for (String line : lines) {
            String clean = line.trim();
            if (!clean.isBlank()) {
                scenes.add(new Scene(idx++, clean));
            }
        }
        return scenes;
    }
}
