package com.gym.fintech_gym_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Sliding-Window Log Algorithm using Redis Sorted Sets (ZSET):
     * 1. Purges all timestamps older than (now - windowDurationMs).
     * 2. Counts requests remaining in the active sliding window.
     * 3. If count >= maxRequests: rejects with false (HTTP 429).
     * 4. Else: records the current request timestamp and allows with true.
     *
     * @param clientKey        Unique identifier (e.g. client IP or User ID)
     * @param maxRequests      Maximum allowed requests per window (e.g. 5)
     * @param windowDurationMs Window size in milliseconds (e.g. 1000ms for 1 second)
     * @return true if request is allowed, false if rate limited
     */
    public boolean isAllowed(String clientKey, int maxRequests, long windowDurationMs) {
        String redisKey = "rate_limit:" + clientKey;
        long now = System.currentTimeMillis();
        long windowStart = now - windowDurationMs;

        try {
            // Step 1: Remove expired timestamps older than the sliding window boundary
            redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

            // Step 2: Count requests remaining in the active sliding window
            Long currentCount = redisTemplate.opsForZSet().zCard(redisKey);

            if (currentCount != null && currentCount >= maxRequests) {
                log.warn("Rate limit EXCEEDED for client '{}': {} requests in last {}ms (Limit: {})",
                        clientKey, currentCount, windowDurationMs, maxRequests);
                return false;
            }

            // Step 3: Add current request timestamp (score = timestamp, unique member value)
            String member = now + ":" + UUID.randomUUID().toString().substring(0, 8);
            redisTemplate.opsForZSet().add(redisKey, member, (double) now);

            // Step 4: Set a short TTL so idle Redis keys expire automatically
            redisTemplate.expire(redisKey, Duration.ofMillis(windowDurationMs * 2));

            return true;

        } catch (Exception e) {
            // Fail-Open Pattern: If Redis blips, log a warning but allow traffic so we don't bring down the API
            log.warn("Rate limiter failed to check Redis, failing open: {}", e.getMessage());
            return true;
        }
    }
}
