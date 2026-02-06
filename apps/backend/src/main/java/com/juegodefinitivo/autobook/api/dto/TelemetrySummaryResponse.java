package com.juegodefinitivo.autobook.api.dto;

import java.util.Map;

public record TelemetrySummaryResponse(
        long totalEvents,
        Map<String, Long> byEvent,
        Map<String, Long> byStage
) {
}

