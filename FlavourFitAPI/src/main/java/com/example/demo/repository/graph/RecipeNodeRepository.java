package com.example.demo.repository.graph;

import com.example.demo.model.graph.RecipeNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeNodeRepository extends Neo4jRepository<RecipeNode, String> {

    @Query("""
        MATCH (u:User {id: $userId}), (r:Recipe {id: $recipeId})
        MERGE (u)-[:CREATED]->(r)
        """)
    void createCreatedRelationship(String userId, String recipeId);
}
