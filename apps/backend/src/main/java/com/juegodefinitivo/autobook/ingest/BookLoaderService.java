package com.juegodefinitivo.autobook.ingest;

import com.juegodefinitivo.autobook.domain.Scene;
import com.juegodefinitivo.autobook.engine.BookParser;

import java.nio.file.Path;
import java.util.List;

public class BookLoaderService {

    private final ExtractorResolver resolver;
    private final BookParser parser;

    public BookLoaderService(ExtractorResolver resolver, BookParser parser) {
        this.resolver = resolver;
        this.parser = parser;
    }

    public List<Scene> loadScenes(Path path, int maxChars, int linesPerChunk) {
        DocumentTextExtractor extractor = resolver.resolve(path);
        String rawText = extractor.extract(path);
        return parser.parseText(rawText, maxChars, linesPerChunk);
    }
}
