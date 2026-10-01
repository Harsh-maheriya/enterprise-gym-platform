package com.gym.fintech_gym_api.controllers;

import com.gym.fintech_gym_api.models.LedgerEntry;
import com.gym.fintech_gym_api.repositories.LedgerEntryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller layer for handling incoming HTTP requests related to Financial Ledgers.
 */
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerController(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    /**
     * GET /api/ledger/{userId}
     * Fetches the chronological billing history for a specific user.
     * We pass the UUID dynamically through the URL path.
     */
    @GetMapping("/{userId}")
    public List<LedgerEntry> getUserLedger(@PathVariable UUID userId) {
        return ledgerEntryRepository.findByUserId(userId);
    }
}
