package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.GameSession;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.engine.BookParser;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.engine.PlayerAction;
import com.juegodefinitivo.autobook.ingest.BookLoaderService;
import com.juegodefinitivo.autobook.ingest.ExtractorResolver;
import com.juegodefinitivo.autobook.ingest.TxtTextExtractor;
import com.juegodefinitivo.autobook.narrative.DialogueService;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EndToEndFlowTest {

    @Test
    void shouldPlaySeveralScenesFromTxtBook(@TempDir Path tempDir) throws Exception {
        Path book = tempDir.resolve("story.txt");
        Files.writeString(book, "Escena uno del bosque.\n\nEscena dos del puente.\n\nEscena tres del castillo.");

        BookLoaderService loader = new BookLoaderService(
                new ExtractorResolver(new TxtTextExtractor(), path -> ""),
                new BookParser()
        );
        NarrativeBuilder builder = new NarrativeBuilder(new AutoQuestionService(new Random(2)), new Random(2));
        GameEngineService engine = new GameEngineService(new DialogueService(), new Random(2));

        var scenes = builder.build(loader.loadScenes(book, 320, 4));
        GameSession session = new GameSession();

        for (int i = 0; i < Math.min(3, scenes.size()); i++) {
            engine.apply(session, scenes.get(i), PlayerAction.TALK, false, null);
            engine.moveNextScene(session, scenes.size());
        }

        assertTrue(session.getCurrentScene() >= 3 || session.isCompleted());
        assertTrue(session.getKnowledge() > 0);
    }
}
