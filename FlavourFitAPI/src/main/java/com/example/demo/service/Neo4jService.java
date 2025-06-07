package com.example.demo.service;

import com.example.demo.model.graph.RecipeNode;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class Neo4jService {

    private final Neo4jClient neo4jClient;

    public Neo4jService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Collection<RecipeNode> getLikedRecipes(String userId) {
        return neo4jClient.query("""
            MATCH (u:User {id: $id})-[:LIKES]->(r:Recipe)
            RETURN r.id AS id, r.name AS name
        """)
                .bind(userId).to("id")
                .fetchAs(RecipeNode.class)
                .mappedBy((typeSystem, record) -> {
                    RecipeNode recipe = new RecipeNode();
                    recipe.setId(record.get("id").asString(null));
                    recipe.setName(record.get("name").asString(null));
                    return recipe;
                })
                .all();
    }
}
