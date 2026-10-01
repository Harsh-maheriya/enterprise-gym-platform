package com.gym.fintech_gym_api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable Record representing a row in the 'ledger_entries' table.
 */
@Table("ledger_entries")
public record LedgerEntry(
    @Id 
    UUID id,
    
    UUID userId,
    UUID paymentId, // This can be null
    String transactionType,
    
    // We strictly use BigDecimal for money, NEVER Double or Float.
    BigDecimal amount, 
    
    String description,
    OffsetDateTime createdAt
) {}
