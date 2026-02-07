package com.juegodefinitivo.autobook.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiSecurityProperties {

    private final boolean enabled;
    private final String studentToken;
    private final String teacherToken;
    private final String adminToken;

    public ApiSecurityProperties(
            @Value("${app.security.enabled:true}") boolean enabled,
            @Value("${app.security.student-token:dev-student-token}") String studentToken,
            @Value("${app.security.teacher-token:dev-teacher-token}") String teacherToken,
            @Value("${app.security.admin-token:dev-admin-token}") String adminToken
    ) {
        this.enabled = enabled;
        this.studentToken = studentToken == null ? "" : studentToken.trim();
        this.teacherToken = teacherToken == null ? "" : teacherToken.trim();
        this.adminToken = adminToken == null ? "" : adminToken.trim();
    }

    public boolean enabled() {
        return enabled;
    }

    public String studentToken() {
        return studentToken;
    }

    public String teacherToken() {
        return teacherToken;
    }

    public String adminToken() {
        return adminToken;
    }
}
