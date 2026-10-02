package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.models.dto.BookingRequest;
import com.gym.fintech_gym_api.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * POST /api/bookings
     * Expects a JSON payload (BookingRequest) containing the userId and classSessionId.
     */
    @PostMapping
    public ResponseEntity<String> bookClassSession(@RequestBody BookingRequest request) {
        try {
            // Hand the request to our secure service layer
            String result = bookingService.bookClass(request);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // If the database lock throws an error (e.g., class is full, or user already booked),
            // we catch it and return a clean 400 HTTP error to the frontend.
            return ResponseEntity.badRequest().body("Booking Failed: " + e.getMessage());
        }
    }
}
