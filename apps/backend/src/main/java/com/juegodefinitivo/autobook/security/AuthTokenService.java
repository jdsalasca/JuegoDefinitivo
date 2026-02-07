package com.juegodefinitivo.autobook.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthTokenService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private final ApiSecurityProperties properties;
    private final ObjectMapper objectMapper;

    public AuthTokenService(ApiSecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public IssuedToken issue(String username, ApiRole role) {
        try {
            long issuedAt = Instant.now().getEpochSecond();
            long expiresAt = issuedAt + properties.jwtTtlSeconds();
            Map<String, Object> payload = Map.of(
                    "sub", username,
                    "role", role.name(),
                    "iat", issuedAt,
                    "exp", expiresAt
            );
            String payloadJson = objectMapper.writeValueAsString(payload);
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signature = sign(payloadEncoded);
            return new IssuedToken(payloadEncoded + "." + signature, expiresAt);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo emitir token de autenticacion.", ex);
        }
    }

    public Optional<VerifiedToken> verify(String token) {
        try {
            if (token == null || token.isBlank()) {
                return Optional.empty();
            }
            String[] parts = token.trim().split("\\.", 2);
            if (parts.length != 2) {
                return Optional.empty();
            }
            String payloadEncoded = parts[0];
            String signature = parts[1];
            String expected = sign(payloadEncoded);
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadEncoded);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, MAP_TYPE);
            String username = String.valueOf(payload.get("sub"));
            String roleValue = String.valueOf(payload.get("role"));
            long expiresAt = Long.parseLong(String.valueOf(payload.get("exp")));
            if (Instant.now().getEpochSecond() >= expiresAt) {
                return Optional.empty();
            }
            ApiRole role = ApiRole.valueOf(roleValue);
            return Optional.of(new VerifiedToken(username, role, expiresAt));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String sign(String payloadEncoded) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(properties.jwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);
        byte[] digest = mac.doFinal(payloadEncoded.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public record IssuedToken(String token, long expiresAtEpochSeconds) {
    }

    public record VerifiedToken(String username, ApiRole role, long expiresAtEpochSeconds) {
    }
}
