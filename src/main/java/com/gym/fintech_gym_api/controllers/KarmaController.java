package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.services.KarmaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/karma")
public class KarmaController {

    private final KarmaService karmaService;

    public KarmaController(KarmaService karmaService) {
        this.karmaService = karmaService;
    }

    /**
     * PUT /api/karma/complete-session/{sessionId}
     * Triggered by an instructor when a class finishes.
     */
    @PutMapping("/complete-session/{sessionId}")
    public ResponseEntity<String> completeSession(@PathVariable UUID sessionId) {
        try {
            String result = karmaService.completeClassSession(sessionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * POST /api/karma/users/{userId}/redeem/{rewardId}
     * Triggered when a user tries to buy a physical reward using their points.
     */
    @PostMapping("/users/{userId}/redeem/{rewardId}")
    public ResponseEntity<String> redeemReward(@PathVariable UUID userId, @PathVariable UUID rewardId) {
        try {
            // Hands the user ID and reward ID down to our secure transactional service
            String result = karmaService.redeemReward(userId, rewardId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // If the database constraint throws an error (e.g., they don't have enough points),
            // it will roll back and return a clean 400 Bad Request to the frontend!
            return ResponseEntity.badRequest().body("Redemption Failed: " + e.getMessage());
        }
    }
}
