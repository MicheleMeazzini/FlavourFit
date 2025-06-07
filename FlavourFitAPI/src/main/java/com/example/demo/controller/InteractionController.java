package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.document.Interaction;
import com.example.demo.model.interactionCrud.CreateInteractionInput;
import com.example.demo.repository.document.UserRepository;
import com.example.demo.service.InteractionService;
import com.example.demo.utils.AuthorizationUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/interaction")
public class InteractionController {

    @Autowired
    private InteractionService service;
    private final AuthorizationUtil authorizationUtil;
    private final UserRepository userRepository;

    public InteractionController(AuthorizationUtil authorizationUtil, UserRepository userRepository) {
        this.authorizationUtil = authorizationUtil;
        this.userRepository = userRepository;
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public List<Interaction> getAllInteractions() {
        return service.getAllInteractions();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Optional<Interaction> getInteractionById(@PathVariable String id) {
        return service.getInteractionById(id);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public Interaction createInteraction(@RequestBody CreateInteractionInput createInteractionInput) {
        return service.createInteraction(createInteractionInput);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateInteraction(@PathVariable String id, @RequestBody Interaction interaction, HttpServletRequest request) {
        Optional<Interaction> existing = service.getInteractionById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Interaction not found"));
        }

        String authorUsername = existing.get().getAuthor();
        String authorId = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Author not found")).get_id();

        boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, authorId);
        if (!authorized) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("No access to update interaction"));
        }
        service.updateInteraction(id, interaction); // la tua logica attuale
        return ResponseEntity.ok(new GenericOkMessage("Interaction updated"));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ErrorMessage> deleteInteraction(@PathVariable String id, HttpServletRequest request) throws Exception {

        Optional<Interaction> interaction = service.getInteractionById(id);if (interaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Recipe not found"));
        }

        String authorUsername = interaction.get().getAuthor();

        String authorId = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("User not found")).get_id();

        boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, authorId);
        if(!authorized)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to delete interaction"));
        service.deleteInteraction(id);
        return null;
    }
}
