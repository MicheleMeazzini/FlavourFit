package com.example.demo.model.graph;

import lombok.Data;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Data
@Node("User")
public class UserNode {
    @Id
    @Property("id")
    private String id;
    private String name;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> followedUsers = new HashSet<>();

    @Relationship(type = "LIKES", direction = Relationship.Direction.OUTGOING)
    private Set<RecipeNode> likedRecipes = new HashSet<>();

    @Relationship(type = "CREATED", direction = Relationship.Direction.OUTGOING)
    private Set<RecipeNode> createdRecipes = new HashSet<>();
}
