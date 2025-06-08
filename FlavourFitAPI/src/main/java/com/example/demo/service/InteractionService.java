package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import java.util.Map.Entry;
import java.util.Map;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.interactionCrud.CreateInteractionInput;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.utils.Enumerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;

    public InteractionService(InteractionRepository interactionRepository, RecipeRepository recipeRepository, RecipeService recipeService) {
        this.interactionRepository = interactionRepository;
        this.recipeRepository = recipeRepository;
        this.recipeService = recipeService;
    }

    public List<Interaction> getAllInteractions() {
        return interactionRepository.findAll();
    }

    public List<Interaction> getInteractionsByRecipeId(String recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));
        return interactionRepository.findAllById(recipe.getInteractions());
    }

    public Optional<Interaction> getInteractionById(String id) {
        return interactionRepository.findById(id);
    }

    @Transactional
    public Interaction createInteraction(CreateInteractionInput createInteractionInput) {
        Interaction interaction = new Interaction();

        interaction.setReview(createInteractionInput.getReview());
        interaction.setRating(createInteractionInput.getRating());
        interaction.setDate(new Date());
        interaction.setAuthor(createInteractionInput.getAuthor());

        Interaction savedInteraction = interactionRepository.save(interaction);

        recipeService.addInteractionToRecipe(createInteractionInput.getRecipe_id(), savedInteraction.get_id());

        return savedInteraction;
    }

    @Transactional
    public Interaction updateInteraction(String id, Interaction updatedInteraction) {
        return interactionRepository.findById(id).map(interaction -> {
            interaction.setReview(updatedInteraction.getReview());
            interaction.setRating(updatedInteraction.getRating());
            interaction.setDate(updatedInteraction.getDate());
            interaction.setAuthor(updatedInteraction.getAuthor());
            return interactionRepository.save(interaction);
        }).orElse(null);
    }

    @Transactional
    public Enumerators.InteractionError patchInteraction(String id, Map<String, Object> params) {
        Optional<Interaction> interactionOpt = interactionRepository.findById(id);
        if (interactionOpt.isEmpty()) {
            return Enumerators.InteractionError.INTERACTION_NOT_FOUND;
        }

        Interaction interaction = interactionOpt.get();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            try {
                switch (field) {
                    case "review" -> {
                        if (value instanceof String)
                            interaction.setReview((String) value);
                        else
                            return Enumerators.InteractionError.INVALID_FIELD;
                    }

                    case "rating" -> {
                        if (value instanceof Number)
                            interaction.setRating(((Number) value).intValue());
                        else
                            return Enumerators.InteractionError.INVALID_FIELD;
                    }

                    default -> {
                        return Enumerators.InteractionError.INVALID_FIELD;
                    }
                }
            } catch (Exception e) {
                return Enumerators.InteractionError.INVALID_FIELD;
            }
        }

        interaction.setDate(new Date()); // aggiorna sempre data

        try {
            interactionRepository.save(interaction);
            return Enumerators.InteractionError.NO_ERROR;
        } catch (Exception e) {
            System.out.println("Error saving interaction: " + e.getMessage());
            return Enumerators.InteractionError.GENERIC_ERROR;
        }
    }

    @Transactional
    public void deleteInteraction(String id) throws Exception {
        Optional<Recipe> recipe = recipeRepository.findRecipeByInteractionId(id);

        if (recipe.isEmpty()) {
            throw new Exception("Interaction not associated with any recipe");
        }

        Recipe savedRecipe = recipe.get();
        savedRecipe.getInteractions().remove(id);

        recipeRepository.save(savedRecipe);

        interactionRepository.deleteById(id);
    }

}
