package com.juegodefinitivo.autobook.ingest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BookImportService {

    private final Path booksDir;

    public BookImportService(Path booksDir) {
        this.booksDir = booksDir;
    }

    public BookAsset importFromInput(String rawPath) {
        Path sourcePath = parsePath(rawPath);
        if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("No existe el archivo indicado: " + sourcePath);
        }

        try {
            Files.createDirectories(booksDir);
            String cleanName = sourcePath.getFileName().toString().replaceAll("\\s+", "-").toLowerCase();
            Path destination = booksDir.resolve(cleanName);
            Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
            String format = destination.getFileName().toString().toLowerCase().endsWith(".pdf") ? "pdf" : "txt";
            return new BookAsset(destination.getFileName().toString(), destination.toAbsolutePath().normalize(), format);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo importar el libro", e);
        }
    }

    Path parsePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalArgumentException("Ruta vacia");
        }

        String input = rawPath.trim();
        if (input.startsWith("file:///")) {
            return Path.of(URI.create(input)).toAbsolutePath().normalize();
        }
        return Path.of(input).toAbsolutePath().normalize();
    }
}
