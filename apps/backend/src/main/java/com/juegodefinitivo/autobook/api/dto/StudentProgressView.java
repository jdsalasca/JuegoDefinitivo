package com.juegodefinitivo.autobook.api.dto;

public record StudentProgressView(
        String studentId,
        String studentName,
        int attempts,
        int completedAttempts,
        int averageScore,
        int averageCorrectAnswers,
        int averageProgressPercent,
        int averageEffectiveMinutes,
        String dominantDifficulty
) {
}

