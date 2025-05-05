package com.example.demo.service;

import com.example.demo.model.Interaction;
import com.example.demo.model.interactionCrud.CreateInteractionInput;
import com.example.demo.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InteractionService {

    @Autowired
    private InteractionRepository repository;

    public List<Interaction> getAllInteractions() {
        return repository.findAll();
    }

    public Optional<Interaction> getInteractionById(String id) {
        return repository.findById(id);
    }

    public Interaction createInteraction(CreateInteractionInput createInteractionInput) {
        Interaction interaction = new Interaction();

        interaction.setReview(createInteractionInput.getReview());
        interaction.setRating(createInteractionInput.getRating());
        interaction.setDate(new Date());
        interaction.setAuthor(createInteractionInput.getAuthor());

        // Logica per mettere l'interaction nell'array in Recipe

        return repository.save(interaction);
    }

    public Interaction updateInteraction(String id, Interaction updatedInteraction) {
        return repository.findById(id).map(interaction -> {
            interaction.setReview(updatedInteraction.getReview());
            interaction.setRating(updatedInteraction.getRating());
            interaction.setDate(updatedInteraction.getDate());
            interaction.setAuthor(updatedInteraction.getAuthor());
            return repository.save(interaction);
        }).orElse(null);
    }

    public void deleteInteraction(String id) {
        repository.deleteById(id);
    }
}
