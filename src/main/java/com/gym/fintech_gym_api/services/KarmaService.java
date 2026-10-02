package com.gym.fintech_gym_api.services;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KarmaService {

    private final JdbcClient jdbcClient;

    public KarmaService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Called by an instructor at the end of a class.
     * @Transactional guarantees that if any part of this fails, nobody gets paid twice.
     */
    @Transactional
    public String completeClassSession(UUID sessionId) {
        
        String currentStatus = jdbcClient.sql("SELECT status FROM class_sessions WHERE id = :sessionId FOR UPDATE")
                .param("sessionId", sessionId)
                .query(String.class)
                .single();

        if ("COMPLETED".equals(currentStatus)) {
            throw new RuntimeException("Class is already completed! Cannot award points twice.");
        }

        Integer karmaReward = jdbcClient.sql("SELECT karma_reward FROM class_sessions WHERE id = :sessionId")
                .param("sessionId", sessionId)
                .query(Integer.class)
                .single();

        if (karmaReward > 0) {
            List<UUID> attendees = jdbcClient.sql("SELECT user_id FROM bookings WHERE class_session_id = :sessionId AND status = 'CONFIRMED'")
                    .param("sessionId", sessionId)
                    .query(UUID.class)
                    .list();

            for (UUID userId : attendees) {
                
                jdbcClient.sql("INSERT INTO karma_ledger (user_id, amount, transaction_type, reference_id) VALUES (:userId, :amount, 'CLASS_REWARD', :sessionId)")
                        .param("userId", userId)
                        .param("amount", karmaReward)
                        .param("sessionId", sessionId)
                        .update();

                jdbcClient.sql("""
                        INSERT INTO karma_balances (user_id, current_balance, updated_at) 
                        VALUES (:userId, :amount, NOW())
                        ON CONFLICT (user_id) 
                        DO UPDATE SET current_balance = karma_balances.current_balance + :amount, updated_at = NOW()
                        """)
                        .param("userId", userId)
                        .param("amount", karmaReward)
                        .update();
            }
        }

        jdbcClient.sql("UPDATE class_sessions SET status = 'COMPLETED' WHERE id = :sessionId")
                .param("sessionId", sessionId)
                .update();

        return "Class completed successfully. Karma points and balances updated for all attendees!";
    }

    /**
     * Called when a user buys a reward using their points.
     */
    @Transactional
    public String redeemReward(UUID userId, UUID rewardId) {
        
        // 1. Fetch the cost of the item
        Integer cost = jdbcClient.sql("SELECT points_cost FROM reward_catalog WHERE id = :rewardId AND is_active = true")
                .param("rewardId", rewardId)
                .query(Integer.class)
                .single();

        // 2. Make it a negative number for our ledger math
        Integer deductionAmount = -cost;

        // 3. Generate the receipt ID right here in Java
        UUID redemptionId = UUID.randomUUID();

        // 4. Create the physical receipt for the front desk staff
        jdbcClient.sql("INSERT INTO reward_redemptions (id, user_id, reward_id, status) VALUES (:id, :userId, :rewardId, 'PENDING_FULFILLMENT')")
                .param("id", redemptionId)
                .param("userId", userId)
                .param("rewardId", rewardId)
                .update();

        // 5. Log it in the strict audit ledger
        jdbcClient.sql("INSERT INTO karma_ledger (user_id, amount, transaction_type, reference_id) VALUES (:userId, :amount, 'REWARD_CLAIM', :refId)")
                .param("userId", userId)
                .param("amount", deductionAmount)
                .param("refId", redemptionId)
                .update();

        // 6. Update the fast-cache balance (The database CHECK constraint will throw an error here if they are broke)
        jdbcClient.sql("""
                INSERT INTO karma_balances (user_id, current_balance, updated_at) 
                VALUES (:userId, :amount, NOW())
                ON CONFLICT (user_id) 
                DO UPDATE SET current_balance = karma_balances.current_balance + :amount, updated_at = NOW()
                """)
                .param("userId", userId)
                .param("amount", deductionAmount)
                .update();

        return "Reward redeemed successfully! Please pick up your item at the front desk.";
    }
}
