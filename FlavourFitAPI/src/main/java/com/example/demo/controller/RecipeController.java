package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.graph.RecipeNode;
import com.example.demo.service.RecipeService;
import com.example.demo.service.UserService;
import com.example.demo.utils.AuthorizationUtil;
import com.example.demo.utils.Enumerators;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final AuthorizationUtil authorizationUtil;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, AuthorizationUtil authorizationUtil, UserService userService) {
        this.recipeService = recipeService;
        this.authorizationUtil = authorizationUtil;
        this.userService = userService;
    }

     // Read All Recipes
     @GetMapping()
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
    @GetMapping("/{id}")
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

    @GetMapping("/search/{partialName}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> searchRecipeNodes(@PathVariable String partialName) {
        try {
            List<RecipeNode> recipes = recipeService.searchRecipeNodesByPartialName(partialName);
            return ResponseEntity.ok(recipes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Search failed: " + e.getMessage());
        }
    }

    @GetMapping("/tag/{tag}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Recipe>> getRecipesByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "50") int limit) {

        List<Recipe> recipes = recipeService.findRecipesByTag(tag, limit);
        return ResponseEntity.ok(recipes);
    }

    // Create a Recipe
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe,HttpServletRequest request) {
        String creatorId = recipe.getAuthor();

        try {
            if (!authorizationUtil.verifyOwnershipOrAdmin(request, creatorId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access denied");
            }
            recipeService.createRecipe(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            String errorMessage = "Failed to create recipe";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }
    
    // Delete a Recipe
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteRecipe(@PathVariable String id, HttpServletRequest request) throws Exception {
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(id);

        if (recipeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Recipe not found"));
        }

        Recipe recipe = recipeOpt.get();

        try
        {
            /*
            boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, recipe.getAuthor_id());
            if(!authorized)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to delete recipe"));

             */
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok(new GenericOkMessage("Recipe successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe,HttpServletRequest request) throws Exception {
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(id);

        if (recipeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Recipe not found"));
        }

        Recipe recipe = recipeOpt.get();
        try {
            boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, recipe.getAuthor_id());
            if(!authorized)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to update recipe"));
            recipeService.updateRecipe(id, updatedRecipe);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            String errorMessage = "Failed to update recipe: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }

    @PatchMapping(value = "/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateRecipe(@RequestBody Map<String, Object> params, @PathVariable String id,HttpServletRequest request) throws Exception {
        Optional<Recipe> recipeOpt = recipeService.getRecipeById(id);

        if (recipeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Recipe not found"));
        }

        Recipe recipe = recipeOpt.get();
        try {
            boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, recipe.getAuthor_id());
            if(!authorized)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to update recipe"));

            Enumerators.RecipeError result = recipeService.updateRecipe(id, params);
            switch (result) {
                case MISSING_NAME -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Name not found"));
                }
                case NO_ERROR -> {
                    return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("Recipe successfully updated"));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
                }
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/created-by/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public List<RecipeNode> getCreatedRecipes(@PathVariable String userId) {
        return recipeService.getRecipesCreatedByUser(userId);
    }

    @GetMapping("/liked-by-followed/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public List<RecipeNode> getRecipesLikedByFollowed(@PathVariable String userId) {
        return recipeService.getRecipesLikedByFollowedUsers(userId);
    }

    @GetMapping("/popular")
    @SecurityRequirement(name = "bearerAuth")
    public List<RecipeNode> getPopularRecipes() {
        return recipeService.getMostLikedRecipes();
    }
}
