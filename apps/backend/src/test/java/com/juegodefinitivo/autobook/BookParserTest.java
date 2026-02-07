package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.BookParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookParserTest {

    private final BookParser parser = new BookParser();

    @Test
    void shouldSplitByParagraphs(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("book.txt");
        Files.writeString(file, "Parrafo uno.\n\nParrafo dos.");

        List<Scene> scenes = parser.parse(file, 500, 4);

        assertEquals(2, scenes.size());
        assertEquals("Parrafo uno.", scenes.get(0).text());
    }

    @Test
    void shouldFallbackToLineChunksWhenNoParagraphs(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("book.txt");
        Files.writeString(file, "l1\nl2\nl3\nl4\nl5\nl6");

        List<Scene> scenes = parser.parse(file, 500, 3);

        assertFalse(scenes.isEmpty());
        assertEquals(2, scenes.size());
    }

    @Test
    void shouldSplitByChapterHeaders() {
        String text = """
                Capitulo 1: Inicio
                El caballero entra al bosque.

                Capitulo 2: Puente
                Cruza el puente y aprende algo nuevo.
                """;

        List<Scene> scenes = parser.parseText(text, 500, 4);

        assertEquals(2, scenes.size());
        assertTrue(scenes.get(0).text().contains("Capitulo 1: Inicio"));
        assertTrue(scenes.get(1).text().contains("Capitulo 2: Puente"));
    }
}
