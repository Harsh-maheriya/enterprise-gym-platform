package com.gym.fintech_gym_api.services;

import com.gym.fintech_gym_api.models.dto.BookingRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final JdbcClient jdbcClient;

    public BookingService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * @Transactional ensures that the database lock is safely released 
     * even if the Java code crashes halfway through.
     */
    @Transactional
    public String bookClass(BookingRequest request) {
        
        // INTERVIEW FLEX: Pessimistic Row-Level Locking
        // The "FOR UPDATE" command freezes this specific class row in Postgres.
        // If 100 people click 'Book' at the same millisecond, Postgres forces 99 of them 
        // to wait in line until this transaction finishes.
        Integer maxCapacity = jdbcClient.sql("SELECT max_capacity FROM class_sessions WHERE id = :sessionId FOR UPDATE")
                .param("sessionId", request.classSessionId())
                .query(Integer.class)
                .single();

        // 2. Count how many people are currently confirmed
        Integer currentBookings = jdbcClient.sql("SELECT COUNT(*) FROM bookings WHERE class_session_id = :sessionId AND status = 'CONFIRMED'")
                .param("sessionId", request.classSessionId())
                .query(Integer.class)
                .single();

        // 3. The Capacity Check
        if (currentBookings >= maxCapacity) {
            throw new RuntimeException("Class is completely full.");
        }

        // 4. Secure the spot
        // (Remember our Composite Primary Key? If a user accidentally sends this request twice, 
        // the database will automatically throw an error here, preventing double-booking).
        jdbcClient.sql("INSERT INTO bookings (class_session_id, user_id, status) VALUES (:sessionId, :userId, 'CONFIRMED')")
                .param("sessionId", request.classSessionId())
                .param("userId", request.userId())
                .update();

        return "Booking successful! Spot secured.";
    }
}
