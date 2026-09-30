package com.gym.fintech_gym_api.repositories;

import com.gym.fintech_gym_api.models.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository layer for the users table.
 * We are strictly using raw SQL via JdbcClient instead of a heavy ORM (like Hibernate).
 * This demonstrates explicit control over database queries and performance.
 */
@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Retrieves all users from the database.
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        
        return jdbcClient.sql(sql)
                .query(User.class)
                .list();
    }

    /**
     * Finds a specific user by their email address.
     * Uses named parameters (:email) to completely prevent SQL injection attacks.
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        
        return jdbcClient.sql(sql)
                .param("email", email)
                .query(User.class)
                .optional();
    }
}
