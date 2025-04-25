package com.example.demo.controller;

import com.example.demo.model.Interaction;
import com.example.demo.service.InteractionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/interaction")
public class InteractionController {

    @Autowired
    private InteractionService service;

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
    public Interaction createInteraction(@RequestBody Interaction interaction) {
        return service.createInteraction(interaction);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Interaction updateInteraction(@PathVariable String id, @RequestBody Interaction interaction) {
        return service.updateInteraction(id, interaction);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteInteraction(@PathVariable String id) {
        service.deleteInteraction(id);
    }
}
