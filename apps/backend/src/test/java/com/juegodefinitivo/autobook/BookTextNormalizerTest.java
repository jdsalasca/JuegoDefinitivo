package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.ingest.BookTextNormalizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTextNormalizerTest {

    private final BookTextNormalizer normalizer = new BookTextNormalizer();

    @Test
    void shouldRemoveRepeatedHeadersFootersAndPageNumbers() {
        String raw = """
                El Caballero de la Armadura Oxidada
                Capitulo uno inicia aqui.
                1
                \f
                El Caballero de la Armadura Oxidada
                Continua la aventura.
                2
                \f
                El Caballero de la Armadura Oxidada
                Cierre del capitulo.
                3
                """;

        String clean = normalizer.normalize(raw);

        assertFalse(clean.contains("El Caballero de la Armadura Oxidada"));
        assertFalse(clean.contains("\n1\n"));
        assertFalse(clean.contains("\n2\n"));
        assertTrue(clean.contains("Capitulo uno inicia aqui."));
        assertTrue(clean.contains("Continua la aventura."));
        assertTrue(clean.contains("Cierre del capitulo."));
    }

    @Test
    void shouldMergeHyphenatedLineBreaks() {
        String raw = """
                Esta es una histo-
                ria para ninos.
                """;

        String clean = normalizer.normalize(raw);

        assertTrue(clean.contains("historia para ninos."));
    }
}

