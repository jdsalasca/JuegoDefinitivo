package com.juegodefinitivo.autobook.api.dto;

import java.util.List;
import java.util.Map;

public record NarrativeGraphResponse(
        String sessionId,
        Map<String, Integer> nodes,
        List<NarrativeLinkView> links
) {
}

