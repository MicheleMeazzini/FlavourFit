package com.example.demo.repository;

import com.example.demo.model.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Specificare modello e di che tipo è l'ID
@Repository
public interface InteractionRepository extends MongoRepository<Interaction, Integer> {

    List<Interaction> getInteractionByAuthor(String author);
}
