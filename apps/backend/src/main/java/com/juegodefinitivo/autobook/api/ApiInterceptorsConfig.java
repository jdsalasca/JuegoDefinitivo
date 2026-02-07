package com.juegodefinitivo.autobook.api;

import com.juegodefinitivo.autobook.security.ApiAuthInterceptor;
import com.juegodefinitivo.autobook.security.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiInterceptorsConfig implements WebMvcConfigurer {

    private final ApiAuthInterceptor apiAuthInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public ApiInterceptorsConfig(ApiAuthInterceptor apiAuthInterceptor, RateLimitInterceptor rateLimitInterceptor) {
        this.apiAuthInterceptor = apiAuthInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAuthInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
    }
}
