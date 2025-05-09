package com.example.demo.service;

import com.example.demo.model.Interaction;
import com.example.demo.model.interactionCrud.CreateInteractionInput;
import com.example.demo.repository.InteractionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final RecipeService recipeService;

    public InteractionService(InteractionRepository interactionRepository, RecipeService recipeService) {
        this.interactionRepository = interactionRepository;
        this.recipeService = recipeService;
    }

    public List<Interaction> getAllInteractions() {
        return interactionRepository.findAll();
    }

    public Optional<Interaction> getInteractionById(String id) {
        return interactionRepository.findById(id);
    }

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

    public Interaction updateInteraction(String id, Interaction updatedInteraction) {
        return interactionRepository.findById(id).map(interaction -> {
            interaction.setReview(updatedInteraction.getReview());
            interaction.setRating(updatedInteraction.getRating());
            interaction.setDate(updatedInteraction.getDate());
            interaction.setAuthor(updatedInteraction.getAuthor());
            return interactionRepository.save(interaction);
        }).orElse(null);
    }

    public void deleteInteraction(String id) {
        interactionRepository.deleteById(id);
    }
}
