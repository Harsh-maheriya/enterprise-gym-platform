package com.gym.fintech_gym_api.models.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for returning class schedule details.
 * Serialized to JSON when stored in Redis for cache-aside reads.
 */
public record ClassScheduleResponse(
    UUID id,
    String className,
    String facilityName,
    String trainerEmail,
    Instant startTime,
    Instant endTime,
    int maxCapacity,
    int karmaReward,
    String status
) {}
