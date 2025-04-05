package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Specificare modello e di che tipo è l'ID
@Repository
public interface UserRepository extends MongoRepository<User, Integer> {
}
