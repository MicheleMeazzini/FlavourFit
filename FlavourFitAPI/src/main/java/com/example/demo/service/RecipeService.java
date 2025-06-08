package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.document.User;
import com.example.demo.model.graph.RecipeNode;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.graph.RecipeNodeRepository;
import com.example.demo.repository.graph.UserNodeRepository;
import com.example.demo.utils.Enumerators;
import com.mongodb.DuplicateKeyException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeNodeRepository recipeNodeRepository;
    private final UserNodeRepository userNodeRepository;
    private final InteractionRepository interactionRepository;
    private final MongoService mongoService;
    private final Neo4jService neo4jService;
    private final RecipeServiceTransactional recipeServiceTransactional;

    public RecipeService(RecipeRepository recipeRepository, InteractionRepository interactionRepository, RecipeNodeRepository recipeNodeRepository, UserNodeRepository userNodeRepository, MongoService mongoService, Neo4jService neo4jService, RecipeServiceTransactional recipeServiceTransactional) {
        this.recipeRepository = recipeRepository;
        this.recipeNodeRepository = recipeNodeRepository;
        this.interactionRepository = interactionRepository;
        this.userNodeRepository = userNodeRepository;
        this.mongoService = mongoService;
        this.neo4jService = neo4jService;
        this.recipeServiceTransactional = recipeServiceTransactional;
    }

    //<editor-fold desc="Get Recipe operations">

    public List<Recipe> getAllRecipes() throws Exception {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) throws Exception {
        return recipeRepository.findById(id);
    }

    //</editor-fold>

    //<editor-fold desc="Recipe Delete operations">

    public boolean deleteFromNeo4j(String id) {
        try {
            recipeNodeRepository.deleteRecipeNodeAndRelationships(id);
        } catch (Exception e) {
            System.out.println("Error deleting UserNode from Neo4j: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void deleteRecipe(String id) throws Exception {
        RecipeNode backupNode = neo4jService.findRecipeWithAllRelationships(id)
                .orElseThrow(() -> new Exception("Recipe not found in Neo4j"));

        boolean deletedFromNeo4j = deleteFromNeo4j(id);
        if (!deletedFromNeo4j) {
            throw new Exception("Failed to delete recipe from Neo4j");
        }
        try {
            recipeServiceTransactional.deleteRecipeFromMongoDb(id);
        } catch (Exception e) {
            // rollback in caso di errore
            recipeNodeRepository.save(backupNode);
            throw new Exception("Failed to delete recipe from MongoDB: " + e.getMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Recipe Update operations">

    public Enumerators.RecipeError updateRecipe(String id, Recipe updatedRecipe) throws Exception {
        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isEmpty()) {
            return Enumerators.RecipeError.RECIPE_NOT_FOUND;
        }

        boolean neo4jUpdateNeeded = !updatedRecipe.getName().equals(existingRecipeOpt.get().getName());
        RecipeNode backupNode = neo4jService.findRecipeWithAllRelationships(id)
                .orElseThrow(() -> new Exception("Recipe not found in Neo4j"));
        if (neo4jUpdateNeeded) {
            RecipeNode recipeNode = new RecipeNode();
            recipeNode.setId(updatedRecipe.get_id());
            recipeNode.setName(updatedRecipe.getName());
            recipeNodeRepository.save(recipeNode);
        }
        try {
            recipeServiceTransactional.updateRecipeInMongoDb(updatedRecipe);
            return Enumerators.RecipeError.NO_ERROR;
        } catch (Exception e) {
            System.out.println("Error during saving: " + e.getMessage());
            if (neo4jUpdateNeeded)
                recipeNodeRepository.save(backupNode);
            return Enumerators.RecipeError.GENERIC_ERROR;
        }
    }

    public Enumerators.RecipeError updateRecipe(String id, Map<String, Object> params) throws Exception {
        Optional<Recipe> recipe = getRecipeById(id);
        if (recipe.isEmpty()) {
            return Enumerators.RecipeError.RECIPE_NOT_FOUND;
        }

        Recipe rcp = recipe.get();
        boolean neo4jUpdateNeeded = false;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {
                case "name":
                    String newName = (String) fieldValue;
                    rcp.setName(newName);
                    neo4jUpdateNeeded = true;
                    break;

                case "minutes":
                    int newMinutes = (int) fieldValue;
                    rcp.setMinutes(newMinutes);
                    break;

                case "tags":
                    rcp.setTags((List<String>) fieldValue);
                    break;

                case "nutrition":
                    rcp.setNutrition((List<Double>) fieldValue);
                    break;

                case "steps":
                    rcp.setSteps((List<String>) fieldValue);
                    break;

                case "description":
                    String newDescription = (String) fieldValue;
                    rcp.setDescription(newDescription);
                    break;

                case "interactions":
                    List<String> interactions = recipe.get().getInteractions();
                    interactions.addAll((List<String>) fieldValue);
                    rcp.setInteractions(interactions);
                    break;

                case "ingredient":
                    rcp.setIngredient((List<Recipe.Ingredient>) fieldValue);
                    break;

                default:
                    throw new IllegalArgumentException("Field not updatable: " + fieldName);
            }
        }
        RecipeNode backupNode = neo4jService.findRecipeWithAllRelationships(id)
                .orElseThrow(() -> new Exception("Recipe not found in Neo4j"));
        if (neo4jUpdateNeeded) {
            RecipeNode recipeNode = new RecipeNode();
            recipeNode.setId(rcp.get_id());
            recipeNode.setName(rcp.getName());
            recipeNodeRepository.save(recipeNode);
        }
        try {
            recipeServiceTransactional.updateRecipeInMongoDb(rcp);
            return Enumerators.RecipeError.NO_ERROR;
        } catch (Exception e) {
            System.out.println("Error during saving: " + e.getMessage());
            if (neo4jUpdateNeeded)
                recipeNodeRepository.save(backupNode);
            return Enumerators.RecipeError.GENERIC_ERROR;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Recipe Create operations">

    public void createRecipe(Recipe recipe) throws Exception {
        Date now = new Date();
        recipe.setDate(now);

        Recipe savedRecipe = recipeRepository.save(recipe);

        RecipeNode recipeNode = new RecipeNode();
        recipeNode.setId(savedRecipe.get_id());
        recipeNode.setName(savedRecipe.getName());
        recipeNode.setDate(now);
        try {
            recipeNodeRepository.save(recipeNode);
            recipeNodeRepository.createCreatedRelationship(savedRecipe.getAuthor_id(), savedRecipe.get_id());
        } catch (Exception e) {
            System.out.println("Error creating RecipeNode in Neo4j: " + e.getMessage());
            // Rollback MongoDB save if Neo4j save fails
            recipeRepository.deleteById(savedRecipe.get_id());
            recipeNodeRepository.deleteRecipeNodeAndRelationships(savedRecipe.get_id());
        }
    }


    //</editor-fold>

    //<editor-fold desc="Graph Operations">

    public List<RecipeNode> getRecipesCreatedByUser(String userId) {
        return recipeNodeRepository.findRecipesCreatedByUser(userId);
    }

    public List<RecipeNode> getRecipesLikedByFollowedUsers(String userId) {
        return recipeNodeRepository.findRecipesLikedByFollowedUsers(userId);
    }

    public List<RecipeNode> getMostLikedRecipes() {
        return recipeNodeRepository.findMostLikedRecipes();
    }

    //</editor-fold>

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
}

@Component
class RecipeServiceTransactional {

    private final RecipeRepository recipeRepository;
    private final InteractionRepository interactionRepository;

    RecipeServiceTransactional(RecipeRepository recipeRepository, InteractionRepository interactionRepository) {
        this.recipeRepository = recipeRepository;
        this.interactionRepository = interactionRepository;

    }

    //<editor-fold desc="Mongo Recipe operations">

    @org.springframework.transaction.annotation.Transactional("transactionManager")
    public void deleteRecipeFromMongoDb(String id) throws Exception {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            throw new IllegalArgumentException("Recipe not found");
        }

        Recipe recipe = recipeOpt.get();
        List<Interaction> interactions = interactionRepository.findAllById(recipe.getInteractions());
        interactionRepository.deleteAll(interactions);
        //if(true) throw new RuntimeException("Simulated failure during recipe deletion"); // Simulate a failure
        recipeRepository.deleteById(id);
    }

    @Transactional("transactionManager")
    public void updateRecipeInMongoDb(Recipe updatedRecipe) throws Exception {
        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(updatedRecipe.get_id());
        if (existingRecipeOpt.isPresent()) {
            recipeRepository.save(updatedRecipe);
        } else {
            throw new IllegalArgumentException("Recipe not found");
        }
    }


    //</editor-fold>
}
