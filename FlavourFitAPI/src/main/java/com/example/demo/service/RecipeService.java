package com.example.demo.service;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Enumerators;
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

    public Optional<Recipe> getRecipeById(int id) throws Exception {
        return recipeRepository.findById(id);
    }

    public void createRecipe(Recipe recipe) throws Exception {
        recipeRepository.save(recipe);
    }

    public void deleteRecipe(int id) throws Exception {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        } else {
            throw new Exception("Recipe not found");
        }
    }


}
