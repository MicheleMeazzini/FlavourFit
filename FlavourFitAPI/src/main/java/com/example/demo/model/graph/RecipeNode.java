package com.example.demo.model.graph;

import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Date;
import java.util.List;

@Data
@Node("Recipe")
public class RecipeNode {
    @Id
    @Property("id")
    private String id;
    private String name;
    private Date date;

    @Relationship(type = "CREATE", direction = Relationship.Direction.INCOMING)
    private UserNode creator;

    @Relationship(type = "LIKES", direction = Relationship.Direction.INCOMING)
    private List<UserNode> likedBy;
}
