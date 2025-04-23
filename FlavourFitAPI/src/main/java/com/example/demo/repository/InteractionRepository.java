package com.example.demo.repository;

import com.example.demo.model.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String> {

    List<Interaction> getInteractionByAuthor(String author);
}
