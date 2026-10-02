package com.gym.fintech_gym_api.models.dto;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) catching the JSON payload when a user books a class.
 */
public record BookingRequest(
    UUID userId,
    UUID classSessionId
) {}
