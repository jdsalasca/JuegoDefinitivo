package com.juegodefinitivo.autobook.ingest;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class BookTextNormalizer {

    private static final Pattern PAGE_NUMBER_LINE = Pattern.compile("^(?:p(?:ag(?:ina)?)?\\.?\\s*)?[0-9ivxlcdm]{1,8}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOC_DOTTED_LINE = Pattern.compile("^[\\p{L}\\p{N} ,;:()'\"-]{4,}\\.{2,}\\s*[0-9ivxlcdm]{1,8}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    public String normalize(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        String normalizedLineBreaks = rawText
                .replace("\uFEFF", "")
                .replace("\u00AD", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n');

        List<List<String>> pages = splitIntoPages(normalizedLineBreaks);
        HeaderFooterProfile profile = buildProfile(pages);

        List<String> resultLines = new ArrayList<>();
        for (int p = 0; p < pages.size(); p++) {
            List<String> filtered = filterNoise(pages.get(p), profile);
            List<String> merged = mergeWrappedLines(mergeHyphenatedBreaks(filtered));
            if (!resultLines.isEmpty()) {
                resultLines.add("");
            }
            resultLines.addAll(merged);
        }

        return collapseBlankRuns(resultLines).trim();
    }

    private List<List<String>> splitIntoPages(String text) {
        String[] rawPages = text.split("\\f+");
        List<List<String>> pages = new ArrayList<>();
        for (String page : rawPages) {
            String[] rawLines = page.split("\n");
            List<String> lines = new ArrayList<>();
            for (String rawLine : rawLines) {
                String line = compact(rawLine);
                lines.add(line);
            }
            pages.add(lines);
        }
        if (pages.isEmpty()) {
            pages.add(List.of());
        }
        return pages;
    }

    private HeaderFooterProfile buildProfile(List<List<String>> pages) {
        Map<String, Integer> headerCount = new HashMap<>();
        Map<String, Integer> footerCount = new HashMap<>();

        for (List<String> page : pages) {
            String header = firstNonBlank(page);
            String footer = lastNonBlank(page);
            if (isHeaderFooterCandidate(header)) {
                headerCount.merge(signature(header), 1, Integer::sum);
            }
            if (isHeaderFooterCandidate(footer)) {
                footerCount.merge(signature(footer), 1, Integer::sum);
            }
        }

        return new HeaderFooterProfile(headerCount, footerCount, Math.max(2, pages.size() / 3));
    }

    private List<String> filterNoise(List<String> page, HeaderFooterProfile profile) {
        List<String> output = new ArrayList<>();
        String header = firstNonBlank(page);
        String footer = lastNonBlank(page);
        String headerSig = signature(header);
        String footerSig = signature(footer);

        for (int i = 0; i < page.size(); i++) {
            String line = page.get(i);
            if (line.isBlank()) {
                output.add("");
                continue;
            }
            if (PAGE_NUMBER_LINE.matcher(line).matches()) {
                continue;
            }
            if (TOC_DOTTED_LINE.matcher(line).matches()) {
                continue;
            }
            if (looksLikeTableOfContents(line)) {
                continue;
            }
            if (i == indexOfFirstNonBlank(page)
                    && profile.headerCount().getOrDefault(headerSig, 0) >= profile.minRepetitions()) {
                continue;
            }
            if (i == indexOfLastNonBlank(page)
                    && profile.footerCount().getOrDefault(footerSig, 0) >= profile.minRepetitions()) {
                continue;
            }
            output.add(line);
        }
        return output;
    }

    private List<String> mergeHyphenatedBreaks(List<String> lines) {
        List<String> merged = new ArrayList<>();
        int i = 0;
        while (i < lines.size()) {
            String current = lines.get(i);
            if (current.isBlank()) {
                merged.add("");
                i++;
                continue;
            }
            if (current.endsWith("-") && i + 1 < lines.size()) {
                String next = lines.get(i + 1);
                if (!next.isBlank() && startsWithLowercase(next)) {
                    merged.add(current.substring(0, current.length() - 1) + next);
                    i += 2;
                    continue;
                }
            }
            merged.add(current);
            i++;
        }
        return merged;
    }

    private List<String> mergeWrappedLines(List<String> lines) {
        List<String> merged = new ArrayList<>();
        int i = 0;
        while (i < lines.size()) {
            String current = lines.get(i);
            if (current.isBlank()) {
                merged.add("");
                i++;
                continue;
            }
            StringBuilder paragraph = new StringBuilder(current);
            int cursor = i + 1;
            while (cursor < lines.size()) {
                String next = lines.get(cursor);
                if (next.isBlank()) {
                    break;
                }
                if (!shouldJoin(paragraph.toString(), next)) {
                    break;
                }
                paragraph.append(' ').append(next);
                cursor++;
            }
            merged.add(paragraph.toString());
            i = cursor;
        }
        return merged;
    }

    private String collapseBlankRuns(List<String> lines) {
        StringBuilder out = new StringBuilder();
        boolean lastBlank = false;
        for (String line : lines) {
            if (line.isBlank()) {
                if (!lastBlank) {
                    out.append('\n');
                }
                lastBlank = true;
            } else {
                if (out.length() > 0 && !lastBlank) {
                    out.append('\n');
                }
                out.append(line);
                lastBlank = false;
            }
        }
        return out.toString();
    }

    private String compact(String value) {
        if (value == null) {
            return "";
        }
        return MULTI_SPACE.matcher(value.trim()).replaceAll(" ");
    }

    private boolean startsWithLowercase(String line) {
        int codePoint = line.codePointAt(0);
        return Character.isLetter(codePoint) && Character.isLowerCase(codePoint);
    }

    private boolean shouldJoin(String previous, String next) {
        if (previous.endsWith(".")
                || previous.endsWith("!")
                || previous.endsWith("?")
                || previous.endsWith(":")
                || previous.endsWith(";")) {
            return false;
        }
        if (next.startsWith("- ")
                || next.matches("^[0-9]+[\\).].*")
                || next.matches("^[ivxlcdm]+[\\).].*")) {
            return false;
        }
        return startsWithLowercase(next) || previous.length() >= 45;
    }

    private boolean looksLikeTableOfContents(String line) {
        String normalized = line.toLowerCase(Locale.ROOT);
        return (normalized.startsWith("capitulo ")
                || normalized.startsWith("chapter ")
                || normalized.startsWith("seccion ")
                || normalized.startsWith("parte "))
                && normalized.matches(".*\\b[0-9ivxlcdm]{1,8}$");
    }

    private boolean isHeaderFooterCandidate(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }
        int length = line.length();
        return length >= 6 && length <= 90;
    }

    private String signature(String line) {
        if (line == null) {
            return "";
        }
        return line
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\d+", "#")
                .replaceAll("[^\\p{L}# ]", "")
                .trim();
    }

    private String firstNonBlank(List<String> lines) {
        for (String line : lines) {
            if (!line.isBlank()) {
                return line;
            }
        }
        return "";
    }

    private String lastNonBlank(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (!lines.get(i).isBlank()) {
                return lines.get(i);
            }
        }
        return "";
    }

    private int indexOfFirstNonBlank(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfLastNonBlank(List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (!lines.get(i).isBlank()) {
                return i;
            }
        }
        return -1;
    }

    private record HeaderFooterProfile(
            Map<String, Integer> headerCount,
            Map<String, Integer> footerCount,
            int minRepetitions
    ) {
    }
}

