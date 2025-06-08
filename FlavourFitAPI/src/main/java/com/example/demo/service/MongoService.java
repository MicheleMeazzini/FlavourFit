package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.document.User;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.document.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MongoService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final InteractionRepository interactionRepository;
    private final RecipeService recipeService;
    private final InteractionService interactionService;

    public MongoService(UserRepository userRepository, RecipeRepository recipeRepository, InteractionRepository interactionRepository, @Lazy RecipeService recipeService, @Lazy InteractionService interactionService) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.interactionRepository = interactionRepository;
        this.recipeService = recipeService;
        this.interactionService = interactionService;
    }

    //<editor-fold desc="Mongo User operations">

    @Transactional("transactionManager")
    public void deleteUserFromMongoDb(String id) throws Exception {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        String username = user.getUsername();

        List<Recipe> recipes = recipeRepository.getRecipeByAuthor(username);
        for (Recipe recipe : recipes) {
            recipeService.deleteRecipe(recipe.get_id()); // lascia fallire se serve
        }

        List<Interaction> interactions = interactionRepository.getInteractionByAuthor(username);
        for (Interaction interaction : interactions) {
            interactionService.deleteInteraction(interaction.get_id());
        }

        // Simula timeout/errore
        // Thread.sleep(70000); // oppure:
        //if(true) throw new RuntimeException("PORCODIDIO");
        userRepository.deleteById(id);

    }

    @Transactional("transactionManager")
    public void updateUserInMongoDb(User updatedUser, String oldUsername) {
        userRepository.save(updatedUser);

        List<Recipe> recipes = recipeRepository.getRecipeByAuthor(oldUsername);
        for (Recipe recipe : recipes) {
            recipe.setAuthor(updatedUser.getUsername());
            recipeRepository.save(recipe);
        }

        List<Interaction> interactions = interactionRepository.getInteractionByAuthor(oldUsername);
        for (Interaction interaction : interactions) {
            interaction.setAuthor(updatedUser.getUsername());
            interactionRepository.save(interaction);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Mongo Recipe operations">

    @Transactional("transactionManager")
    public void deleteRecipeFromMongoDb(String id) throws Exception {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            throw new IllegalArgumentException("Recipe not found");
        }

        Recipe recipe = recipeOpt.get();
        List<Interaction> interactions = interactionRepository.findAllById(recipe.getInteractions());
        for (Interaction interaction : interactions) {
            interactionService.deleteInteraction(interaction.get_id());
        }

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

