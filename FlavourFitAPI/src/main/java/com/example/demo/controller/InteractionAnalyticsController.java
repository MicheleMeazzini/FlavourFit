package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.repository.document.InteractionRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions/analytics")
public class InteractionAnalyticsController {

    private final InteractionRepository interactionRepository;

    public InteractionAnalyticsController(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @GetMapping("/review-stats")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserReviewStats() {
        var data = interactionRepository.getUserReviewStats();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessuna statistica disponibile."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/rating-distribution")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getRatingDistribution() {
        var data = interactionRepository.getRatingDistribution();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessuna distribuzione disponibile."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/top-reviewers")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getTopReviewers() {
        var data = interactionRepository.findTopReviewers();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessun utente recensore trovato."));
        }
        return ResponseEntity.ok(data);
    }
}


