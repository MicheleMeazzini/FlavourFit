package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.aggregations.UsersPerMonth;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Specificare modello e di che tipo è l'ID
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Aggregation(pipeline = {
            "{ $group: { _id: { $dateToString: { format: '%Y-%m', date: '$registration_date' } }, count: { $sum: 1 } } }",
            "{ $sort: { _id: 1 } }"
    })
    List<UsersPerMonth> countUsersByMonth();


}
