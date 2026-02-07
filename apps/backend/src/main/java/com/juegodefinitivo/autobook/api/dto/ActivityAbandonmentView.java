package com.juegodefinitivo.autobook.api.dto;

public record ActivityAbandonmentView(
        String eventType,
        int activeAttempts,
        int activeRatePercent
) {
}
