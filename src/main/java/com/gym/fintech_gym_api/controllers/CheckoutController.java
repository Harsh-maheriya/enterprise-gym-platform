package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.models.dto.CheckoutRequest;
import com.gym.fintech_gym_api.services.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer exposing the transactional checkout endpoint.
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * POST /api/checkout
     * Expects a JSON payload (CheckoutRequest) in the request body.
     */
    @PostMapping
    public ResponseEntity<String> processCheckout(@RequestBody CheckoutRequest request) {
        try {
            // Hands the payload down to the Service layer where the @Transactional magic happens
            String resultMessage = checkoutService.processPayment(request);
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            // If the transaction fails or the database blocks it (e.g., due to constraints),
            // it rolls back and returns a 400 Bad Request to the frontend.
            return ResponseEntity.badRequest().body("Transaction Failed: " + e.getMessage());
        }
    }
}
