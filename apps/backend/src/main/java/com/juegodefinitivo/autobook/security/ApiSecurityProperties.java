package com.juegodefinitivo.autobook.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiSecurityProperties {

    private final boolean enabled;
    private final String studentToken;
    private final String teacherToken;
    private final String adminToken;
    private final String studentUsername;
    private final String studentPassword;
    private final String teacherUsername;
    private final String teacherPassword;
    private final String adminUsername;
    private final String adminPassword;
    private final String jwtSecret;
    private final String jwtPreviousSecret;
    private final long jwtTtlSeconds;
    private final boolean allowLegacyToken;

    public ApiSecurityProperties(
            @Value("${app.security.enabled:true}") boolean enabled,
            @Value("${app.security.student-token:dev-student-token}") String studentToken,
            @Value("${app.security.teacher-token:dev-teacher-token}") String teacherToken,
            @Value("${app.security.admin-token:dev-admin-token}") String adminToken,
            @Value("${app.security.student-username:student}") String studentUsername,
            @Value("${app.security.student-password:student-pass}") String studentPassword,
            @Value("${app.security.teacher-username:teacher}") String teacherUsername,
            @Value("${app.security.teacher-password:teacher-pass}") String teacherPassword,
            @Value("${app.security.admin-username:admin}") String adminUsername,
            @Value("${app.security.admin-password:admin-pass}") String adminPassword,
            @Value("${app.security.jwt-secret:change-this-secret-in-production}") String jwtSecret,
            @Value("${app.security.jwt-previous-secret:}") String jwtPreviousSecret,
            @Value("${app.security.jwt-ttl-seconds:28800}") long jwtTtlSeconds,
            @Value("${app.security.allow-legacy-token:true}") boolean allowLegacyToken
    ) {
        this.enabled = enabled;
        this.studentToken = studentToken == null ? "" : studentToken.trim();
        this.teacherToken = teacherToken == null ? "" : teacherToken.trim();
        this.adminToken = adminToken == null ? "" : adminToken.trim();
        this.studentUsername = studentUsername == null ? "" : studentUsername.trim();
        this.studentPassword = studentPassword == null ? "" : studentPassword.trim();
        this.teacherUsername = teacherUsername == null ? "" : teacherUsername.trim();
        this.teacherPassword = teacherPassword == null ? "" : teacherPassword.trim();
        this.adminUsername = adminUsername == null ? "" : adminUsername.trim();
        this.adminPassword = adminPassword == null ? "" : adminPassword.trim();
        this.jwtSecret = jwtSecret == null ? "" : jwtSecret.trim();
        this.jwtPreviousSecret = jwtPreviousSecret == null ? "" : jwtPreviousSecret.trim();
        this.jwtTtlSeconds = Math.max(300, jwtTtlSeconds);
        this.allowLegacyToken = allowLegacyToken;
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

    public String studentUsername() {
        return studentUsername;
    }

    public String studentPassword() {
        return studentPassword;
    }

    public String teacherUsername() {
        return teacherUsername;
    }

    public String teacherPassword() {
        return teacherPassword;
    }

    public String adminUsername() {
        return adminUsername;
    }

    public String adminPassword() {
        return adminPassword;
    }

    public String jwtSecret() {
        return jwtSecret;
    }

    public long jwtTtlSeconds() {
        return jwtTtlSeconds;
    }

    public String jwtPreviousSecret() {
        return jwtPreviousSecret;
    }

    public boolean allowLegacyToken() {
        return allowLegacyToken;
    }
}
