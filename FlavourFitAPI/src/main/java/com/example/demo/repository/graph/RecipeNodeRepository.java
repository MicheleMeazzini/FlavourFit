package com.example.demo.repository.graph;

import com.example.demo.model.graph.RecipeNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeNodeRepository extends Neo4jRepository<RecipeNode, String> {

    @Query("""
        MATCH (u:User {id: $userId}), (r:Recipe {id: $recipeId})
        MERGE (u)-[:CREATED]->(r)
        """)
    void createCreatedRelationship(String userId, String recipeId);


    /* typical “on-graph” queries */
    @Query("MATCH (u:User {id: $userId})-[:CREATED]->(r:Recipe) RETURN r")
    List<RecipeNode> findRecipesCreatedByUser(String userId);

    @Query("""
           MATCH (me:User {id: $userId})-[:FOLLOWS]->(:User)-[:LIKES]->(r:Recipe)
           RETURN DISTINCT r
           """)
    List<RecipeNode> findRecipesLikedByFollowedUsers(String userId);

    @Query("""
            MATCH (r:Recipe)<-[:LIKES]-()
            RETURN r,COUNT(*) as A
            ORDER BY A DESC
           """)
    List<RecipeNode> findMostLikedRecipes();
}
