package com.gym.fintech_gym_api.config;

import com.gym.fintech_gym_api.services.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_DURATION_MS = 1000; // 1-second sliding window

    private final RateLimiterService rateLimiterService;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extract client IP (respecting proxy headers if behind reverse proxy/load balancer)
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }

        // Apply distributed sliding-window check (5 req/sec per client IP)
        boolean allowed = rateLimiterService.isAllowed(clientIp, MAX_REQUESTS, WINDOW_DURATION_MS);

        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // HTTP 429
            response.setContentType("application/json");
            response.getWriter().write("""
                {"error": "Too Many Requests", "message": "Rate limit exceeded (5 requests/sec). Please slow down."}
            """);
            return false; // Aborts the request before reaching the controller
        }

        return true; // Allows request to proceed
    }
}
