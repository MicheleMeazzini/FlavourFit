package com.example.demo.repository.graph;

import com.example.demo.model.graph.RecipeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeNodeRepository extends Neo4jRepository<RecipeNode, String> {
}
