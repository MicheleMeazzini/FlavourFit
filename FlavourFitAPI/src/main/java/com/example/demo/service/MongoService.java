package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.document.User;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.document.UserRepository;
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

    public MongoService(UserRepository userRepository, RecipeRepository recipeRepository, InteractionRepository interactionRepository, RecipeService recipeService, InteractionService interactionService) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.interactionRepository = interactionRepository;
        this.recipeService = recipeService;
        this.interactionService = interactionService;
    }

    @Transactional("transactionManager")
    public boolean deleteUserFromMongoDb(String id) throws Exception {
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
        if(true) throw new RuntimeException("PORCODIDIO");
        userRepository.deleteById(id);
        return true;
    }
}

