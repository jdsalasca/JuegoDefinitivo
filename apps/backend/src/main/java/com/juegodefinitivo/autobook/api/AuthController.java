package com.juegodefinitivo.autobook.api;

import com.juegodefinitivo.autobook.api.dto.LoginRequest;
import com.juegodefinitivo.autobook.api.dto.LoginResponse;
import com.juegodefinitivo.autobook.security.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password())
                .map(result -> new LoginResponse(
                        result.token(),
                        "Bearer",
                        result.role().name(),
                        result.expiresAtEpochSeconds()
                ))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> onUnauthorized(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void onError(Exception ex) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex);
    }
}
