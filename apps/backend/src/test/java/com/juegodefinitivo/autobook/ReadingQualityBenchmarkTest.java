package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.narrative.EntityExtractor;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import com.juegodefinitivo.autobook.narrative.NarrativeQualityEvaluator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadingQualityBenchmarkTest {

    @Test
    void shouldReachMinimumNarrativeQualityThreshold() {
        List<Scene> baseScenes = List.of(
                new Scene(0, "Capitulo uno. El Caballero avanza por el bosque con Maia y observa un puente antiguo."),
                new Scene(1, "Capitulo dos. En el castillo, Maia plantea un reto de lectura sobre la decision correcta."),
                new Scene(2, "Capitulo tres. Tras la batalla, el grupo descansa en la torre y encuentra una pista."),
                new Scene(3, "Capitulo cuatro. El equipo vuelve a la biblioteca para conectar la historia completa.")
        );

        NarrativeBuilder builder = new NarrativeBuilder(new AutoQuestionService(new Random(7)), new EntityExtractor(), new Random(7));
        NarrativeQualityEvaluator evaluator = new NarrativeQualityEvaluator();

        var narrativeScenes = builder.build(baseScenes);
        var report = evaluator.evaluate(narrativeScenes);

        assertTrue(report.score() >= 60, "Narrative quality should stay above minimum threshold");
    }
}

