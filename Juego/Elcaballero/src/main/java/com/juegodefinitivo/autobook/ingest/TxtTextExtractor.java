package com.juegodefinitivo.autobook.ingest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TxtTextExtractor implements DocumentTextExtractor {
    @Override
    public String extract(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer TXT: " + path, e);
        }
    }
}
