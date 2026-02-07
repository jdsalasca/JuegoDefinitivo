package com.juegodefinitivo.autobook.ingest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BookImportService {

    private final Path booksDir;
    private final long maxImportBytes;

    public BookImportService(Path booksDir) {
        this(booksDir, 25L * 1024 * 1024);
    }

    public BookImportService(Path booksDir, long maxImportBytes) {
        this.booksDir = booksDir;
        this.maxImportBytes = Math.max(1024, maxImportBytes);
    }

    public BookAsset importFromInput(String rawPath) {
        Path sourcePath = parsePath(rawPath);
        if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("No existe el archivo indicado: " + sourcePath);
        }
        if (!Files.isReadable(sourcePath)) {
            throw new IllegalArgumentException("No se puede leer el archivo indicado.");
        }
        String format = detectFormat(sourcePath);
        validateFileSize(sourcePath);

        try {
            Files.createDirectories(booksDir);
            String cleanName = sanitizeName(sourcePath.getFileName().toString(), format);
            Path destination = booksDir.resolve(cleanName);
            Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
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
        if (input.startsWith("file://")) {
            return Path.of(URI.create(input)).toAbsolutePath().normalize();
        }
        return Path.of(input).toAbsolutePath().normalize();
    }

    private String detectFormat(Path sourcePath) {
        String lower = sourcePath.getFileName().toString().toLowerCase();
        if (lower.endsWith(".txt")) {
            return "txt";
        }
        if (lower.endsWith(".pdf")) {
            return "pdf";
        }
        throw new IllegalArgumentException("Formato no soportado. Usa .txt o .pdf");
    }

    private void validateFileSize(Path sourcePath) {
        try {
            long size = Files.size(sourcePath);
            if (size <= 0) {
                throw new IllegalArgumentException("El archivo esta vacio.");
            }
            if (size > maxImportBytes) {
                throw new IllegalArgumentException("El archivo excede el limite permitido (" + maxImportBytes + " bytes).");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo validar el tamano del archivo.", e);
        }
    }

    private String sanitizeName(String originalName, String format) {
        String baseName = originalName.replaceAll("\\.[^.]+$", "");
        String safeBase = baseName
                .toLowerCase()
                .replaceAll("[^a-z0-9-_]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-+|-+$)", "");
        if (safeBase.isBlank()) {
            safeBase = "book-import";
        }
        return safeBase + "." + format;
    }
}
