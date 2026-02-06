package com.juegodefinitivo.autobook.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookCatalogService {

    private final Path booksDir;

    public BookCatalogService(Path booksDir) {
        this.booksDir = booksDir;
    }

    public List<BookAsset> listBooks() {
        List<BookAsset> books = new ArrayList<>();
        try {
            Files.createDirectories(booksDir);
            try (var stream = Files.list(booksDir)) {
                stream.filter(Files::isRegularFile)
                        .filter(this::isSupported)
                        .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                        .forEach(path -> books.add(new BookAsset(
                                path.getFileName().toString(),
                                path.toAbsolutePath().normalize(),
                                getFormat(path)
                        )));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer catalogo de libros", e);
        }
        return books;
    }

    private boolean isSupported(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".pdf");
    }

    private String getFormat(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".pdf") ? "pdf" : "txt";
    }
}
