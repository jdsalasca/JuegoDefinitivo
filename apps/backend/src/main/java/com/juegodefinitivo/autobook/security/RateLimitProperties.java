package com.juegodefinitivo.autobook.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimitProperties {

    private final boolean enabled;
    private final int windowSeconds;
    private final int maxRequestsPerWindow;
    private final int teacherMaxRequestsPerWindow;

    public RateLimitProperties(
            @Value("${app.rate-limit.enabled:true}") boolean enabled,
            @Value("${app.rate-limit.window-seconds:60}") int windowSeconds,
            @Value("${app.rate-limit.max-requests:120}") int maxRequestsPerWindow,
            @Value("${app.rate-limit.teacher-max-requests:300}") int teacherMaxRequestsPerWindow
    ) {
        this.enabled = enabled;
        this.windowSeconds = Math.max(1, windowSeconds);
        this.maxRequestsPerWindow = Math.max(1, maxRequestsPerWindow);
        this.teacherMaxRequestsPerWindow = Math.max(this.maxRequestsPerWindow, teacherMaxRequestsPerWindow);
    }

    public boolean enabled() {
        return enabled;
    }

    public int windowSeconds() {
        return windowSeconds;
    }

    public int maxRequestsPerWindow() {
        return maxRequestsPerWindow;
    }

    public int teacherMaxRequestsPerWindow() {
        return teacherMaxRequestsPerWindow;
    }
}
