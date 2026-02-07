package com.juegodefinitivo.autobook.ingest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;

public class PdfTextExtractor implements DocumentTextExtractor {
    @Override
    public String extract(Path path) {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            StringBuilder allText = new StringBuilder();
            int totalPages = Math.max(1, document.getNumberOfPages());
            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                allText.append(stripper.getText(document));
                if (page < totalPages) {
                    allText.append('\f');
                }
            }
            return allText.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer PDF: " + path, e);
        }
    }
}
