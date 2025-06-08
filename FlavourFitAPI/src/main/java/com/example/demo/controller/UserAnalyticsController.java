package com.example.demo.controller;


import com.example.demo.model.ErrorMessage;
import com.example.demo.repository.document.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/analytics")
public class UserAnalyticsController {

    private final UserRepository userRepository;

    public UserAnalyticsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/registrations-per-month")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getRegistrationsPerMonth() {
        var data = userRepository.countUsersByMonth();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessun utente registrato trovato."));
        }
        return ResponseEntity.ok(data);
    }
}

