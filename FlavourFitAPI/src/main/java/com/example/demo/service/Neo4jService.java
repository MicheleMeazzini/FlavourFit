package com.example.demo.service;

import com.example.demo.model.graph.RecipeNode;
import com.example.demo.model.graph.UserNode;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Neo4jService {

    private final Neo4jClient neo4jClient;

    public Neo4jService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Optional<UserNode> findUserWithAllRelationships(String id) {
        String query = """
            MATCH (u:User {id: $id})
            OPTIONAL MATCH (u)-[:FOLLOWS]->(f:User)
            OPTIONAL MATCH (u)-[:LIKES]->(l:Recipe)
            OPTIONAL MATCH (u)-[:CREATED]->(c:Recipe)
            RETURN u, collect(DISTINCT f) AS followed, collect(DISTINCT l) AS liked, collect(DISTINCT c) AS created
        """;

        return neo4jClient.query(query)
                .bind(id).to("id")
                .fetch()
                .one()
                .map(result -> {
                    // Spring restituisce una Map<String, Object>
                    Object uRaw = result.get("u");
                    if (!(uRaw instanceof Node uNode)) return null;

                    UserNode user = new UserNode();
                    user.setId(uNode.get("id").asString());
                    user.setName(uNode.get("name").asString());

                    // FOLLOWED USERS
                    List<UserNode> followedUsers = new ArrayList<>();
                    Object followedRaw = result.get("followed");
                    if (followedRaw instanceof List<?> rawList) {
                        for (Object obj : rawList) {
                            if (obj instanceof Node fNode) {
                                UserNode f = new UserNode();
                                f.setId(fNode.get("id").asString());
                                f.setName(fNode.get("name").asString());
                                followedUsers.add(f);
                            }
                        }
                    }

                    // LIKED RECIPES
                    List<RecipeNode> likedRecipes = new ArrayList<>();
                    Object likedRaw = result.get("liked");
                    if (likedRaw instanceof List<?> rawList) {
                        for (Object obj : rawList) {
                            if (obj instanceof Node lNode) {
                                RecipeNode r = new RecipeNode();
                                r.setId(lNode.get("id").asString());
                                r.setName(lNode.get("name").asString());
                                likedRecipes.add(r);
                            }
                        }
                    }

                    // CREATED RECIPES
                    List<RecipeNode> createdRecipes = new ArrayList<>();
                    Object createdRaw = result.get("created");
                    if (createdRaw instanceof List<?> rawList) {
                        for (Object obj : rawList) {
                            if (obj instanceof Node cNode) {
                                RecipeNode r = new RecipeNode();
                                r.setId(cNode.get("id").asString());
                                r.setName(cNode.get("name").asString());
                                createdRecipes.add(r);
                            }
                        }
                    }

                    user.setFollowedUsers(followedUsers);
                    user.setLikedRecipes(likedRecipes);
                    user.setCreatedRecipes(createdRecipes);

                    return user;
                });
    }
}
