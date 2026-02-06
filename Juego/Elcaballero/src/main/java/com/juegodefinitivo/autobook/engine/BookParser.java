package com.juegodefinitivo.autobook.engine;

import com.juegodefinitivo.autobook.domain.Scene;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BookParser {

    public List<Scene> parse(Path bookPath, int maxCharsPerScene, int linesPerChunk) {
        String content;
        try {
            content = Files.readString(bookPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el libro: " + bookPath, e);
        }
        return parseText(content, maxCharsPerScene, linesPerChunk);
    }

    public List<Scene> parseText(String content, int maxCharsPerScene, int linesPerChunk) {
        String normalized = content.replace("\r\n", "\n").trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        List<String> rawBlocks = splitByParagraph(normalized);
        if (rawBlocks.size() <= 1) {
            rawBlocks = splitByFixedLines(normalized, Math.max(2, linesPerChunk));
        }

        List<String> scenes = splitLongBlocks(rawBlocks, Math.max(180, maxCharsPerScene));
        List<Scene> output = new ArrayList<>();
        for (int i = 0; i < scenes.size(); i++) {
            output.add(new Scene(i, scenes.get(i)));
        }
        return output;
    }

    private List<String> splitByParagraph(String text) {
        String[] blocks = text.split("\\n\\s*\\n+");
        List<String> result = new ArrayList<>();
        for (String block : blocks) {
            String cleaned = compactWhitespace(block);
            if (!cleaned.isBlank()) {
                result.add(cleaned);
            }
        }
        return result;
    }

    private List<String> splitByFixedLines(String text, int linesPerChunk) {
        String[] lines = text.split("\\n");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int lineCounter = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (current.length() > 0) {
                current.append(' ');
            }
            current.append(trimmed);
            lineCounter++;

            if (lineCounter >= linesPerChunk) {
                chunks.add(compactWhitespace(current.toString()));
                current.setLength(0);
                lineCounter = 0;
            }
        }

        if (current.length() > 0) {
            chunks.add(compactWhitespace(current.toString()));
        }

        return chunks;
    }

    private List<String> splitLongBlocks(List<String> blocks, int maxCharsPerScene) {
        List<String> scenes = new ArrayList<>();
        for (String block : blocks) {
            if (block.length() <= maxCharsPerScene) {
                scenes.add(block);
                continue;
            }

            String[] sentences = block.split("(?<=[.!?])\\s+");
            StringBuilder current = new StringBuilder();
            for (String sentence : sentences) {
                if (current.length() + sentence.length() + 1 > maxCharsPerScene && current.length() > 0) {
                    scenes.add(compactWhitespace(current.toString()));
                    current.setLength(0);
                }
                if (current.length() > 0) {
                    current.append(' ');
                }
                current.append(sentence);
            }
            if (current.length() > 0) {
                scenes.add(compactWhitespace(current.toString()));
            }
        }
        return scenes;
    }

    private String compactWhitespace(String input) {
        return input.replaceAll("\\s+", " ").trim();
    }
}
