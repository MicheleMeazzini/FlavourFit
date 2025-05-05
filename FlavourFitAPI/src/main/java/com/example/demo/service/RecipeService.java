package com.example.demo.service;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Enumerators;
import com.mongodb.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() throws Exception {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) throws Exception {
        return recipeRepository.findById(id);
    }

    public void createRecipe(Recipe recipe) throws Exception {
        recipeRepository.save(recipe);
    }

    public void deleteRecipe(String id) throws Exception {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        } else {
            throw new Exception("Recipe not found");
        }
    }

    public void updateRecipe(String id, Recipe updatedRecipe) throws Exception {
        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isPresent()) {
            updatedRecipe.set_id(id);
            try {
                recipeRepository.save(updatedRecipe);
            } catch (DuplicateKeyException e) {
                throw new Exception("Recipe not updated");
            }
        } else {
            throw new Exception("Recipe not found");
        }
    }

    public Enumerators.RecipeError updateRecipe(String id, Map<String, Object> params) throws Exception {
        Optional<Recipe> recipe = getRecipeById(id);
        if(recipe.isEmpty()){
            return Enumerators.RecipeError.RECIPE_NOT_FOUND;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {
                case "name":
                    String newName = (String) fieldValue;
                    recipe.get().setName(newName);
                    break;

                case "minutes":
                    int newMinutes = (int) fieldValue;
                    recipe.get().setMinutes(newMinutes);
                    break;

                case "tags":
                    recipe.get().setTags((List<String>) fieldValue);
                    break;

                case "nutrition":
                    recipe.get().setNutrition((List<Double>) fieldValue);
                    break;

                case "steps":
                    recipe.get().setSteps((List<String>) fieldValue);
                    break;

                case "description":
                    String newDescription = (String) fieldValue;
                    recipe.get().setDescription(newDescription);
                    break;

                case "interactions":
                    List<String> interactions = recipe.get().getInteractions();
                    interactions.addAll((List<String>) fieldValue);
                    recipe.get().setInteractions(interactions);
                    break;

                case "ingredient":
                    recipe.get().setIngredient((List<Recipe.Ingredient>) fieldValue);
                    break;

                default:
                    throw new IllegalArgumentException("Field not updatable: " + fieldName);
            }
        }

        try {
            recipeRepository.save(recipe.get());
            return Enumerators.RecipeError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Error during saving: " + e.getMessage());
            return Enumerators.RecipeError.GENERIC_ERROR;
        }
    }
}
