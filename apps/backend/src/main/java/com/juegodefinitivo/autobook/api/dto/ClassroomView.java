package com.juegodefinitivo.autobook.api.dto;

public record ClassroomView(
        String id,
        String name,
        String teacherName,
        int students,
        int assignments
) {
}

