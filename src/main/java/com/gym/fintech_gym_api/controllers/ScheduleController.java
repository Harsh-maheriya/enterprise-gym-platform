package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.models.dto.ClassScheduleResponse;
import com.gym.fintech_gym_api.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * GET /api/schedule
     * Fetches active gym class schedule using Redis Cache-Aside.
     */
    @GetMapping
    public ResponseEntity<List<ClassScheduleResponse>> getActiveSchedule() {
        return ResponseEntity.ok(scheduleService.getActiveSchedule());
    }
}
