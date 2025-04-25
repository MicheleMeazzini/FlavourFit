package com.example.demo.service;

import com.example.demo.model.Interaction;
import com.example.demo.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Interaction createInteraction(Interaction interaction) {
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
