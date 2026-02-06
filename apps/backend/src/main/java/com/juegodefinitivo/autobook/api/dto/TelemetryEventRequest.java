package com.juegodefinitivo.autobook.api.dto;

import java.util.Map;

public record TelemetryEventRequest(
        String sessionId,
        String eventName,
        String stage,
        Long elapsedMs,
        Map<String, String> metadata
) {
}

