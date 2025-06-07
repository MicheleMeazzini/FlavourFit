package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.graph.RecipeNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.graph.RecipeNodeRepository;
import com.example.demo.repository.graph.UserNodeRepository;
import com.example.demo.utils.Enumerators;
import com.mongodb.DuplicateKeyException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeNodeRepository recipeNodeRepository;
    private final UserNodeRepository userNodeRepository;
    private final InteractionRepository interactionRepository;

    public RecipeService(RecipeRepository recipeRepository, InteractionRepository interactionRepository, RecipeNodeRepository recipeNodeRepository, UserNodeRepository userNodeRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeNodeRepository = recipeNodeRepository;
        this.interactionRepository = interactionRepository;
        this.userNodeRepository = userNodeRepository;
    }

    public List<Recipe> getAllRecipes() throws Exception {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) throws Exception {
        return recipeRepository.findById(id);
    }

    public void deleteRecipe(String id) throws Exception {
        if (recipeRepository.existsById(id)) {
            Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new Exception("Recipe not found"));

            if (recipe.getInteractions() != null) {
                for (String interaction : recipe.getInteractions()) {
                    interactionRepository.deleteById(interaction);
                }
            }

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

    public void addInteractionToRecipe(String recipeId, String interactionId) {
        recipeRepository.findById(recipeId).ifPresent(recipe -> {
            List<String> interactions = recipe.getInteractions();
            if (interactions == null) {
                interactions = new ArrayList<>();
            }
            interactions.add(interactionId);
            recipe.setInteractions(interactions);
            recipeRepository.save(recipe);
        });
    }

    @Transactional
    public Recipe createRecipe(Recipe recipe) throws Exception {
        Recipe savedRecipe = recipeRepository.save(recipe);

        RecipeNode recipeNode = new RecipeNode();
        recipeNode.setId(savedRecipe.get_id());
        recipeNode.setName(savedRecipe.getName());
        recipeNodeRepository.save(recipeNode);

        String authorUsername = savedRecipe.getAuthor();
        var userNodeOpt = userNodeRepository.findUserNodeById(authorUsername);
        if (userNodeOpt.isEmpty()) {
            throw new Exception("Author UserNode not found in Neo4j");
        }

        recipeNodeRepository.createCreatedRelationship(authorUsername, savedRecipe.get_id());

        return savedRecipe;
    }

    public List<RecipeNode> getRecipesCreatedByUser(String userId) {
        return recipeNodeRepository.findRecipesCreatedByUser(userId);
    }

    public List<RecipeNode> getRecipesLikedByFollowedUsers(String userId) {
        return recipeNodeRepository.findRecipesLikedByFollowedUsers(userId);
    }

    public List<RecipeNode> getMostLikedRecipes() {
        return recipeNodeRepository.findMostLikedRecipes();
    }

}
