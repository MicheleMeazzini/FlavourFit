package com.example.demo.repository.document;

import com.example.demo.model.document.Recipe;
import com.example.demo.model.aggregations.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>
{
    /*
    @Aggregation(pipeline = {
            "{ $unwind: '$interactions' }",
            "{ $lookup: { from: 'interaction', localField: 'interactions', foreignField: '_id', as: 'details_interaction' } }",
            "{ $unwind: '$details_interaction' }",
            "{ $group: { _id: '$name', average_rating: { $avg: '$details_interaction.rating' }, review_number: { $sum: 1 } } }"

    })
    */
    @Aggregation(pipeline = {
            "{ $sort: { createdAt: -1 } }",
            "{ $project: { name: 1, interactions: 1 }}",
            "{ $unwind: '$interactions' }",
            "{ $lookup: { from: 'interaction', localField: 'interactions', foreignField: '_id', as: 'details_interaction' } }",
            "{ $unwind: '$details_interaction' }",
            "{ $group: { _id: '$name', averageRating: { $avg: '$details_interaction.rating' }, reviewCount: { $sum: 1 } } }",
            "{ $sort: { averageRating: -1 } }"
    })
    List<RecipeAverageRating> getRecipeAverageRating();

    @Aggregation(pipeline = {
            "{ $unwind: '$ingredient' }",
            "{ $group: { _id: '$ingredient.name', occurrences: { $sum: 1 } } }",
            "{ $sort: { occurrences: -1 } }",
            "{ $limit: 10 }"
    })
    List<MostUsedIngredient> getMostUsedIngredients();

    @Aggregation(pipeline = {
            "{ $unwind: '$tags' }",
            "{ $group: { _id: '$tags', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }"
    })
    List<RecipeCountByTag> countRecipesByTag();

    @Aggregation(pipeline = {
            "{ $project: { name: 1, ingredientCount: { $size: '$ingredient' } } }",
            "{ $sort: { ingredientCount: -1 } }",
            "{ $limit: 10 }"
    })
    List<RecipeWithIngredientCount> findTopRecipesByIngredientCount();

    @Aggregation(pipeline = {
            "{ $unwind: '$tags' }",
            "{ $group: { _id: '$tags', averageMinutes: { $avg: '$minutes' } } }",
            "{ $sort: { averageMinutes: -1 } }"
    })
    List<AvgMinutesByTag> getAverageMinutesPerTag();

}
