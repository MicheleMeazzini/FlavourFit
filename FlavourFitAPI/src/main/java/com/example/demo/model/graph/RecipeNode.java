package com.example.demo.model.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Date;

@Data
@Node("Recipe")
public class RecipeNode {
    @Id
    @Property("id")
    private String id;
    private String name;
    private Date date;
}
