package com.example.demo.service;

import com.example.demo.model.Interaction;
import com.example.demo.model.User;
import com.example.demo.repository.InteractionRepository;
import com.example.demo.utils.Enumerators;
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

    public Enumerators.InteractionError SaveInteraction(Interaction interaction) throws Exception {

        //NO_ERROR, MISSING_REVIEW, MISSING_RATING, MISSING_AUTHOR, MISSING_USER_ID, MISSING_RECIPE_ID, INVALID_RATING, RECIPE_NOT_FOUND, GENERIC_ERROR
        if(interaction.getReview() == null || interaction.getReview().isEmpty())
            return Enumerators.InteractionError.MISSING_REVIEW;

        if(interaction.getRating() > 5 || interaction.getRating() < 0)
            return Enumerators.InteractionError.INVALID_RATING;

        if(interaction.getAuthor() == null || interaction.getAuthor().isEmpty())
            return Enumerators.InteractionError.MISSING_AUTHOR;

        if(interaction.getUser_id() < 0)
            return Enumerators.InteractionError.MISSING_USER_ID;

        /*
        if(RECIPE DOESN'T EXIST)
            return Enumerators.InteractionError.RECIPE_NOT_FOUND;
        */

        /*
        if(USER DOESN'T EXIST)
            return Enumerators.InteractionError.USER_NOT_FOUND;
        */

        interaction.setDate(new Date());

        try {
            interactionRepository.save(interaction);
            return Enumerators.InteractionError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Error during saving: " + e.getMessage());
            e.printStackTrace();
            return Enumerators.InteractionError.GENERIC_ERROR;
        }
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
