package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.ingest.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTextExtractorTest {

    @Test
    void shouldExtractTextFromPdf(@TempDir Path tempDir) throws Exception {
        Path pdf = tempDir.resolve("book.pdf");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                stream.newLineAtOffset(100, 700);
                stream.showText("El caballero aprende a escuchar su corazon.");
                stream.endText();
            }
            document.save(pdf.toFile());
        }

        PdfTextExtractor extractor = new PdfTextExtractor();
        String text = extractor.extract(pdf).toLowerCase();

        assertTrue(text.contains("caballero"));
        assertTrue(text.contains("corazon"));
    }
}
