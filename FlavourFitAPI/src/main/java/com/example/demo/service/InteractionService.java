package com.example.demo.service;

import com.example.demo.model.Interaction;
import com.example.demo.repository.InteractionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;

    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public List<Interaction> getAllInteractions() throws Exception {
        return interactionRepository.findAll();
    }

    public Optional<Interaction> getInteractionById(int id) throws Exception {
        return interactionRepository.findById(id);
    }

    public void createInteraction(Interaction interaction) throws Exception {
        interactionRepository.save(interaction);
    }

    public void updateInteraction(int id, Interaction updatedInteraction) throws Exception {
        /*
        if (interactionRepository.existsById(id)) {
            updatedInteraction.setId(id);
            interactionRepository.save(updatedInteraction);
        } else {
            throw new Exception("Interaction not found");
        }
        */
    }

    public void deleteInteraction(int id) throws Exception {
        if (interactionRepository.existsById(id)) {
            interactionRepository.deleteById(id);
        } else {
            throw new Exception("Interaction not found");
        }
    }

    public List<Interaction> getInteractionByAuthor(String author){
        return interactionRepository.getInteractionByAuthor(author);
    }
}
