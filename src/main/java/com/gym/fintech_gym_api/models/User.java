package com.gym.fintech_gym_api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable Record representing a row in the 'users' table.
 * Using a Record ensures data cannot be accidentally mutated in memory,
 * a key security practice for financial and enterprise systems.
 */
@Table("users")
public record User(
    @Id 
    UUID id,
    
    String email,
    String passwordHash,
    String firstName,
    String lastName,
    String accountStatus,
    
    // OffsetDateTime is the Java equivalent of PostgreSQL's TIMESTAMPTZ
    OffsetDateTime createdAt, 
    OffsetDateTime updatedAt
) {}
