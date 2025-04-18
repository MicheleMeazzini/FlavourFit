package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.Interaction;
import com.example.demo.model.User;
import com.example.demo.service.InteractionService;
import com.example.demo.utils.Enumerators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/interaction")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    // Read All Interactions
    @GetMapping("/all")
    public ResponseEntity<?> getInteractions() {
        try {
            return ResponseEntity.ok(interactionService.getAllInteractions());
        } catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // Read Interaction By Id
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getInteractionById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(interactionService.getInteractionById(id));
        } catch (Exception e) {
            String errorMessage = "Interaction not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    // Read Interaction By author name
    @GetMapping("/author/{author}")
    public ResponseEntity<?> getInteractionByAuthor(@PathVariable String author) {
        try {
            return ResponseEntity.ok(interactionService.getInteractionByAuthor(author));
        } catch (Exception e) {
            String errorMessage = "Interaction not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    // Create an Interaction
    @PostMapping ()
    public ResponseEntity<?> CreateInteraction(@RequestBody Interaction interaction) throws Exception {

        //NO_ERROR, MISSING_REVIEW, MISSING_RATING, MISSING_AUTHOR, MISSING_USER_ID, MISSING_RECIPE_ID, INVALID_RATING, RECIPE_NOT_FOUND, USER_NOT_FOUND, GENERIC_ERROR

        Enumerators.InteractionError result = interactionService.SaveInteraction(interaction);
        switch(result){
            case MISSING_REVIEW -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Review is needed"));
            }
            case MISSING_RATING -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Rating is needed"));
            }
            case MISSING_AUTHOR -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Author is needed"));
            }
            case MISSING_USER_ID -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("User id is needed"));
            }
            case MISSING_RECIPE_ID -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Recipe id is needed"));
            }
            case RECIPE_NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Recipe not found"));
            }
            case USER_NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("User not found"));
            }
            case NO_ERROR -> {
                return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("Interaction successfully created"));
            }
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
            }
        }
    }

    // Update an Interaction
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateInteraction(@PathVariable int id, @RequestBody Interaction interaction) {
        try {
            interactionService.updateInteraction(id, interaction);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String errorMessage = "Failed to update interaction";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // Delete an Interaction
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteInteraction(@PathVariable int id) {
        try {
            interactionService.deleteInteraction(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String errorMessage = "Failed to delete interaction";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
