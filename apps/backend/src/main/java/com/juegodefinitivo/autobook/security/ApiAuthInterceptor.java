package com.juegodefinitivo.autobook.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

@Component
public class ApiAuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Api-Token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final ApiSecurityProperties properties;
    private final AuthTokenService tokenService;
    private final ObjectMapper objectMapper;

    public ApiAuthInterceptor(ApiSecurityProperties properties, AuthTokenService tokenService, ObjectMapper objectMapper) {
        this.properties = properties;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!properties.enabled()) {
            return true;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if ("/api/health".equals(path) || "/api/auth/login".equals(path)) {
            return true;
        }

        ApiRole role = resolveRole(request);
        if (role == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token API invalido o ausente.");
            return false;
        }

        ApiRole required = requiredRole(path);
        if (!role.canAccess(required)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "Rol insuficiente para este endpoint.");
            return false;
        }
        return true;
    }

    private ApiRole resolveRole(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String bearerToken = authHeader.substring("Bearer ".length()).trim();
            var verified = tokenService.verify(bearerToken);
            if (verified.isPresent()) {
                request.setAttribute("apiRole", verified.get().role().name());
                request.setAttribute("apiUser", verified.get().username());
                return verified.get().role();
            }
        }
        if (!properties.allowLegacyToken()) {
            return null;
        }
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isBlank()) {
            return null;
        }
        String value = token.trim();
        if (!properties.adminToken().isBlank() && properties.adminToken().equals(value)) {
            return ApiRole.ADMIN;
        }
        if (!properties.teacherToken().isBlank() && properties.teacherToken().equals(value)) {
            return ApiRole.TEACHER;
        }
        if (!properties.studentToken().isBlank() && properties.studentToken().equals(value)) {
            return ApiRole.STUDENT;
        }
        return null;
    }

    private ApiRole requiredRole(String path) {
        if (path.startsWith("/api/teacher")) {
            return ApiRole.TEACHER;
        }
        return ApiRole.STUDENT;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), Map.of("error", message));
    }
}
