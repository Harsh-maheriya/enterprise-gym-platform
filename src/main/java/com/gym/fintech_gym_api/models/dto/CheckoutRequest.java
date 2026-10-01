package com.gym.fintech_gym_api.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO).
 * This represents the JSON payload that the frontend (e.g., React/Mobile App)
 * sends to our Java API when a user clicks the "Pay" button.
 */
public record CheckoutRequest(
    UUID userId,
    UUID invoiceId,
    BigDecimal amount,
    String paymentMethod,
    String idempotencyKey
) {}
