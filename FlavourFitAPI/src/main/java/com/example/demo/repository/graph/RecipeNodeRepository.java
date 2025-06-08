package com.example.demo.repository.graph;

import com.example.demo.model.graph.RecipeNode;
import com.example.demo.model.graph.UserNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeNodeRepository extends Neo4jRepository<RecipeNode, String> {

    @Query("""
    MATCH (u:User {id: $userId})
    MATCH (r:Recipe {id: $recipeId})
    MERGE (u)-[:CREATED]->(r)
    """)
    void createCreatedRelationship(String userId, String recipeId);

    @Query("MATCH (r:Recipe {id: $id}) RETURN r")
    Optional<RecipeNode> findRecipeNodeById(String id);

    @Query("MATCH (u:User {id: $userId})-[:CREATED]->(r:Recipe) RETURN r")
    List<RecipeNode> findRecipesCreatedByUser(String userId); // for Home page

    @Query("""
           MATCH (me:User {id: $userId})-[:FOLLOWS]->(:User)-[:LIKES]->(r:Recipe)
           RETURN DISTINCT r
           """)
    List<RecipeNode> findRecipesLikedByFollowedUsers(String userId);

    @Query("""
            MATCH (r:Recipe)<-[:LIKES]-()
            RETURN r,COUNT(*) as A
            ORDER BY A DESC LIMIT 50
           """)
    List<RecipeNode> findMostLikedRecipes(); // for Community page
}
