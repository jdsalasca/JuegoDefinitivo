package com.juegodefinitivo.autobook.security;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ApiSecurityProperties properties;
    private final AuthTokenService tokenService;

    public AuthService(ApiSecurityProperties properties, AuthTokenService tokenService) {
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public Optional<LoginResult> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        String user = username.trim();
        String pass = password.trim();
        ApiRole role = resolveRole(user, pass);
        if (role == null) {
            return Optional.empty();
        }
        AuthTokenService.IssuedToken token = tokenService.issue(user, role);
        return Optional.of(new LoginResult(token.token(), role, token.expiresAtEpochSeconds()));
    }

    private ApiRole resolveRole(String user, String pass) {
        if (properties.adminUsername().equals(user) && properties.adminPassword().equals(pass)) {
            return ApiRole.ADMIN;
        }
        if (properties.teacherUsername().equals(user) && properties.teacherPassword().equals(pass)) {
            return ApiRole.TEACHER;
        }
        if (properties.studentUsername().equals(user) && properties.studentPassword().equals(pass)) {
            return ApiRole.STUDENT;
        }
        return null;
    }

    public record LoginResult(String token, ApiRole role, long expiresAtEpochSeconds) {
    }
}
