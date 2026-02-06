package com.juegodefinitivo.autobook.ingest;

import java.nio.file.Path;

public class ExtractorResolver {

    private final DocumentTextExtractor txtExtractor;
    private final DocumentTextExtractor pdfExtractor;

    public ExtractorResolver(DocumentTextExtractor txtExtractor, DocumentTextExtractor pdfExtractor) {
        this.txtExtractor = txtExtractor;
        this.pdfExtractor = pdfExtractor;
    }

    public DocumentTextExtractor resolve(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        if (name.endsWith(".txt")) {
            return txtExtractor;
        }
        if (name.endsWith(".pdf")) {
            return pdfExtractor;
        }
        throw new IllegalArgumentException("Formato no soportado. Usa .txt o .pdf");
    }
}
