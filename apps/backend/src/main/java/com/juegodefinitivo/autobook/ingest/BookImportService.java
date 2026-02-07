package com.juegodefinitivo.autobook.ingest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
        validateContentSignature(sourcePath, format);

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

    private void validateContentSignature(Path sourcePath, String format) {
        if ("pdf".equals(format)) {
            validatePdfSignature(sourcePath);
            return;
        }
        if ("txt".equals(format)) {
            validateTextSafety(sourcePath);
        }
    }

    private void validatePdfSignature(Path sourcePath) {
        try (InputStream input = Files.newInputStream(sourcePath)) {
            byte[] header = input.readNBytes(5);
            if (header.length < 5
                    || header[0] != '%'
                    || header[1] != 'P'
                    || header[2] != 'D'
                    || header[3] != 'F'
                    || header[4] != '-') {
                throw new IllegalArgumentException("El archivo .pdf no tiene una firma PDF valida.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo validar el contenido del PDF.", e);
        }
    }

    private void validateTextSafety(Path sourcePath) {
        try (InputStream input = Files.newInputStream(sourcePath)) {
            byte[] sample = input.readNBytes(4096);
            if (sample.length == 0) {
                throw new IllegalArgumentException("El archivo esta vacio.");
            }
            for (byte value : sample) {
                if (value == 0) {
                    throw new IllegalArgumentException("El archivo .txt contiene contenido binario no permitido.");
                }
                int unsigned = value & 0xFF;
                boolean allowedControl = unsigned == '\n' || unsigned == '\r' || unsigned == '\t';
                if (unsigned < 32 && !allowedControl) {
                    throw new IllegalArgumentException("El archivo .txt contiene caracteres de control no permitidos.");
                }
            }
            String text = new String(sample, StandardCharsets.UTF_8);
            if (text.trim().isEmpty()) {
                throw new IllegalArgumentException("El archivo .txt no contiene texto legible.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo validar el contenido del TXT.", e);
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
