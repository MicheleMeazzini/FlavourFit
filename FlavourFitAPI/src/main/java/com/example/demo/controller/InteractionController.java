package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.document.Interaction;
import com.example.demo.model.interactionCrud.CreateInteractionInput;
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

    public InteractionController(AuthorizationUtil authorizationUtil) {
        this.authorizationUtil = authorizationUtil;
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
    public Interaction updateInteraction(@PathVariable String id, @RequestBody Interaction interaction) {
        return service.updateInteraction(id, interaction);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ErrorMessage> deleteInteraction(@PathVariable String id, HttpServletRequest request) throws Exception {

        Optional<Interaction> interaction = service.getInteractionById(id);if (interaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Recipe not found"));
        }

        //Recipe recipe = recipeOpt.get();
        boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, id);
        if(!authorized)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to delete user"));
        service.deleteInteraction(id);
        return null;
    }
}
