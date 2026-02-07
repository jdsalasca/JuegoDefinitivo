package com.juegodefinitivo.autobook.narrative;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EntityExtractor {

    private static final Pattern CAPITALIZED_SPAN = Pattern.compile("\\b([A-ZÁÉÍÓÚÑ][\\p{L}]+(?:\\s+[A-ZÁÉÍÓÚÑ][\\p{L}]+){0,2})\\b");
    private static final List<String> LOCATION_HINTS = List.of(
            "bosque", "castillo", "puente", "ciudad", "torre", "cueva", "reino", "aldea", "biblioteca"
    );

    public List<String> extractEntities(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        Set<String> entities = new LinkedHashSet<>();
        Matcher matcher = CAPITALIZED_SPAN.matcher(text);
        while (matcher.find()) {
            String candidate = matcher.group(1).trim();
            if (candidate.length() >= 3 && !isGeneric(candidate)) {
                entities.add(candidate);
            }
            if (entities.size() >= 8) {
                break;
            }
        }

        String lower = text.toLowerCase(Locale.ROOT);
        for (String hint : LOCATION_HINTS) {
            if (lower.contains(hint)) {
                entities.add("Lugar: " + capitalize(hint));
            }
            if (entities.size() >= 10) {
                break;
            }
        }

        return new ArrayList<>(entities);
    }

    private boolean isGeneric(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.equals("capitulo")
                || normalized.equals("chapter")
                || normalized.equals("parte")
                || normalized.equals("seccion");
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}

