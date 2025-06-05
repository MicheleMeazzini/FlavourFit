package com.example.demo.repository.document;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.aggregations.RatingDistribution;
import com.example.demo.model.aggregations.TopReviewer;
import com.example.demo.model.aggregations.UserReviewStats;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String> {

    void deleteByAuthor(String author);

    List<Interaction> getInteractionByAuthor(String author);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$author', totale_review: { $sum: 1 }, media_valutazione_data: { $avg: '$rating' } } }",
            "{ $sort: { totale_review: -1 } }",
            "{ $limit: 10 }"
    })
    List<UserReviewStats> getUserReviewStats();

    @Aggregation(pipeline = {
            "{ $group: { _id: '$rating', count: { $sum: 1 } } }",
            "{ $sort: { _id: 1 } }"
    })
    List<RatingDistribution> getRatingDistribution();

    @Aggregation(pipeline = {
            "{ $group: { _id: '$author', reviewCount: { $sum: 1 } } }",
            "{ $sort: { reviewCount: -1 } }",
            "{ $limit: 10 }"
    })
    List<TopReviewer> findTopReviewers();

}
