package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.ingest.BookImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookImportServiceTest {

    @Test
    void shouldImportFromFileUri(@TempDir Path tempDir) throws Exception {
        Path source = tempDir.resolve("sample.txt");
        Files.writeString(source, "historia base");
        Path catalog = tempDir.resolve("catalog");

        BookImportService service = new BookImportService(catalog);
        String uri = source.toUri().toString();
        var asset = service.importFromInput(uri);

        assertTrue(Files.exists(asset.path()));
        assertTrue(asset.title().endsWith(".txt"));
    }

    @Test
    void shouldRejectUnsupportedExtension(@TempDir Path tempDir) throws Exception {
        Path source = tempDir.resolve("sample.docx");
        Files.writeString(source, "contenido");
        BookImportService service = new BookImportService(tempDir.resolve("catalog"), 1024 * 1024);

        assertThrows(IllegalArgumentException.class, () -> service.importFromInput(source.toString()));
    }

    @Test
    void shouldRejectFilesAboveMaxImportSize(@TempDir Path tempDir) throws Exception {
        Path source = tempDir.resolve("big-book.txt");
        byte[] payload = new byte[3000];
        Arrays.fill(payload, (byte) 'a');
        Files.write(source, payload);
        BookImportService service = new BookImportService(tempDir.resolve("catalog"), 1024);

        assertThrows(IllegalArgumentException.class, () -> service.importFromInput(source.toString()));
    }
}
