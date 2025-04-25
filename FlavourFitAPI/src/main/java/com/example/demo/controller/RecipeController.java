package com.example.demo.controller;

import com.example.demo.model.Recipe;
import com.example.demo.service.RecipeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

     // Read All Recipes
     @GetMapping("/all")
     @SecurityRequirement(name = "bearerAuth")
     public ResponseEntity<?> getRecipe() {
         try {
             return ResponseEntity.ok(recipeService.getAllRecipes());
         } catch (Exception e) {
             String errorMessage = "Internal Server Error";
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
         }
     }

    // Read Recipe By ID
    @GetMapping("/id/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getRecipeById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(recipeService.getRecipeById(id));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Recipe not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    // Create a Recipe
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe) {
        try {
            recipeService.createRecipe(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            String errorMessage = "Failed to create recipe";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }
    
    // Delete a Recipe
    @DeleteMapping("/id/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteRecipe(@PathVariable String id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String errorMessage = "Failed to delete recipe";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }
}
