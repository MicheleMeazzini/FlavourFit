package com.example.demo.model.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("Recipe")
public class RecipeNode {
    @Id
    private String id;
    private String name;
}
