package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.models.User;
import com.gym.fintech_gym_api.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller layer for handling incoming HTTP requests related to Users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    // Dependency Injection: Spring automatically provides the UserRepository
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /api/users
     * Returns a JSON list of all users in the system.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
