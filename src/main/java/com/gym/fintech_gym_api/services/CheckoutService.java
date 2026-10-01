package com.gym.fintech_gym_api.services;

import com.gym.fintech_gym_api.models.dto.CheckoutRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The Business Logic layer. 
 * This is where we orchestrate complex database interactions.
 */
@Service
public class CheckoutService {

    private final JdbcClient jdbcClient;

    public CheckoutService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * The "Interview Flex": @Transactional guarantees ACID compliance.
     * If ANY of the SQL statements below fail (or if the server crashes halfway), 
     * PostgreSQL instantly undoes all of them. No partial data corruption ever occurs.
     */
    @Transactional
    public String processPayment(CheckoutRequest request) {
        
        // 1. Idempotency Check (Has the user's phone already sent this exact request?)
        Integer paymentExists = jdbcClient.sql("SELECT COUNT(*) FROM payments WHERE idempotency_key = :key")
                .param("key", request.idempotencyKey())
                .query(Integer.class)
                .single();
        
        if (paymentExists > 0) {
            return "Payment already processed. Safe retry acknowledged.";
        }

        // 2. Generate a new ID for the payment
        UUID newPaymentId = UUID.randomUUID();

        // 3. Mark the invoice as PAID
        jdbcClient.sql("UPDATE invoices SET status = 'PAID' WHERE id = :invoiceId")
                .param("invoiceId", request.invoiceId())
                .update();

        // 4. Record the Payment Attempt
        jdbcClient.sql("""
            INSERT INTO payments (id, invoice_id, amount_paid, payment_method, status, idempotency_key)
            VALUES (:id, :invoiceId, :amount, :method, 'SUCCESS', :idempKey)
        """)
                .param("id", newPaymentId)
                .param("invoiceId", request.invoiceId())
                .param("amount", request.amount())
                .param("method", request.paymentMethod())
                .param("idempKey", request.idempotencyKey())
                .update();

        // 5. Append to the Immutable Ledger (The absolute source of truth)
        jdbcClient.sql("""
            INSERT INTO ledger_entries (user_id, payment_id, transaction_type, amount, description)
            VALUES (:userId, :paymentId, 'CREDIT', :amount, 'Invoice Payment via API')
        """)
                .param("userId", request.userId())
                .param("paymentId", newPaymentId)
                .param("amount", request.amount())
                .update();

        return "Transaction Successful. Invoice Paid and Ledger Updated.";
    }
}
