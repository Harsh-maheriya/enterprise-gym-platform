package com.gym.fintech_gym_api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.fintech_gym_api.models.dto.ClassScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private static final String CACHE_KEY = "schedule:active";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final JdbcClient jdbcClient;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ScheduleService(JdbcClient jdbcClient, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Cache-Aside Pattern:
     * 1. Check Redis cache first.
     * 2. If HIT: return cached JSON immediately (<2ms, 0 database hits).
     * 3. If MISS: query PostgreSQL database, write to Redis with 5-minute TTL, return schedule.
     */
    public List<ClassScheduleResponse> getActiveSchedule() {
        try {
            String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cachedJson != null && !cachedJson.isBlank()) {
                log.info("Redis Cache HIT for key: {}", CACHE_KEY);
                return objectMapper.readValue(cachedJson, new TypeReference<List<ClassScheduleResponse>>() {});
            }
        } catch (Exception e) {
            log.warn("Redis read failed, falling back to database: {}", e.getMessage());
        }

        log.info("Redis Cache MISS for key: {}. Querying PostgreSQL database...", CACHE_KEY);

        String sql = """
            SELECT 
                cs.id,
                ct.name AS class_name,
                f.name AS facility_name,
                u.email AS trainer_email,
                cs.start_time,
                cs.end_time,
                cs.max_capacity,
                cs.karma_reward,
                cs.status
            FROM class_sessions cs
            JOIN class_types ct ON cs.class_type_id = ct.id
            JOIN facilities f ON cs.facility_id = f.id
            JOIN users u ON cs.trainer_id = u.id
            WHERE cs.status = 'SCHEDULED'
            ORDER BY cs.start_time ASC
        """;

        List<ClassScheduleResponse> schedule = jdbcClient.sql(sql)
                .query(ClassScheduleResponse.class)
                .list();

        try {
            String jsonToCache = objectMapper.writeValueAsString(schedule);
            redisTemplate.opsForValue().set(CACHE_KEY, jsonToCache, CACHE_TTL);
            log.info("Successfully cached {} class sessions in Redis with TTL of {} minutes", schedule.size(), CACHE_TTL.toMinutes());
        } catch (Exception e) {
            log.warn("Failed to write schedule to Redis cache: {}", e.getMessage());
        }

        return schedule;
    }

    /**
     * Evicts the schedule cache from Redis when a class booking alters capacity or session status.
     */
    public void evictScheduleCache() {
        try {
            redisTemplate.delete(CACHE_KEY);
            log.info("Evicted Redis cache key: {}", CACHE_KEY);
        } catch (Exception e) {
            log.warn("Failed to evict Redis cache key: {}", e.getMessage());
        }
    }
}
