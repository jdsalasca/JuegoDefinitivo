package com.juegodefinitivo.autobook.api.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String role,
        long expiresAtEpochSeconds
) {
}
