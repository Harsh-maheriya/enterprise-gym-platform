package com.gym.fintech_gym_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    public WebMvcConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Protect sensitive transactional endpoints against flash-crowd spikes & bot abuse
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/bookings/**", "/api/checkout/**");
    }
}
