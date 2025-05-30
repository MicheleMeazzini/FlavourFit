package com.example.demo.controller;


import com.example.demo.model.ErrorMessage;
import com.example.demo.repository.document.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/analytics")
public class RecipeAnalyticsController {

    private final RecipeRepository recipeRepository;

    public RecipeAnalyticsController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/average-rating")
    public ResponseEntity<?> getAverageRating() {
        var data = recipeRepository.getRecipeAverageRating();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessuna ricetta valutata."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/most-used-ingredients")
    public ResponseEntity<?> getMostUsedIngredients() {
        var data = recipeRepository.getMostUsedIngredients();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessun ingrediente trovato."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-tag")
    public ResponseEntity<?> countRecipesByTag() {
        var data = recipeRepository.countRecipesByTag();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessun tag trovato."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/most-ingredients")
    public ResponseEntity<?> getRecipesWithMostIngredients() {
        var data = recipeRepository.findTopRecipesByIngredientCount();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessuna ricetta trovata."));
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/average-minutes-by-tag")
    public ResponseEntity<?> getAverageMinutesByTag() {
        var data = recipeRepository.getAverageMinutesPerTag();
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Nessun dato disponibile per i tag."));
        }
        return ResponseEntity.ok(data);
    }
}


