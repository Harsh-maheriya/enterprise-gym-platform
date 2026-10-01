package com.gym.fintech_gym_api.repositories;

import com.gym.fintech_gym_api.models.LedgerEntry;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class LedgerEntryRepository {

    private final JdbcClient jdbcClient;

    public LedgerEntryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Fetches the entire financial history for a specific user.
     * We use ORDER BY created_at DESC so the newest transactions appear first,
     * exactly like a bank statement.
     */
    public List<LedgerEntry> findByUserId(UUID userId) {
        String sql = """
            SELECT * FROM ledger_entries 
            WHERE user_id = :userId 
            ORDER BY created_at DESC
        """;
        
        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(LedgerEntry.class)
                .list();
    }
}
