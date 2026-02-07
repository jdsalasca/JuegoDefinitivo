package com.juegodefinitivo.autobook.api.dto;

import java.util.List;

public record ClassroomDashboardResponse(
        String classroomId,
        String classroomName,
        String teacherName,
        int students,
        int assignments,
        int activeAttempts,
        int completedAttempts,
        int abandonmentRatePercent,
        int totalEffectiveReadingMinutes,
        int averageEffectiveMinutesPerAttempt,
        List<ActivityAbandonmentView> abandonmentByActivity,
        List<StudentProgressView> studentProgress
) {
}

