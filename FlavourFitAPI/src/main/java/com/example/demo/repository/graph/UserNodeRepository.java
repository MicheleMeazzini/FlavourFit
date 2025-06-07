package com.example.demo.repository.graph;

import com.example.demo.model.graph.RecipeNode;
import com.example.demo.model.graph.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, String> {
    @Query("MATCH (u:User {id: $id}) DETACH DELETE u")
    void deleteUserNodeAndRelationships(String id);


    @Query("MATCH (u:User {id: $id}) RETURN u")
    Optional<UserNode> findUserNodeById(String id);

    @Query("""
    MATCH (u:User {id: $id})-[:FOLLOWS]->(f:User)
    RETURN f
    """)
    List<UserNode> findFollowedUsersByUserId(String id);

    @Query("""
    MATCH (u:User {id: $id})-[:LIKES]->(r:Recipe)
    RETURN r
    """)
    List<RecipeNode> findLikedRecipesByUserId(String id);

    @Query("""
    MATCH (u:User {id: $id})-[:CREATED]->(r:Recipe)
    RETURN r
    """)
    List<RecipeNode> findCreatedRecipesByUserId(String id);

    @Query("""
    MATCH (u:User {id: $id})
    OPTIONAL MATCH (u)-[:FOLLOWS]->(f:User)
    OPTIONAL MATCH (u)-[:LIKES]->(l:Recipe)
    OPTIONAL MATCH (u)-[:CREATED]->(c:Recipe)
    RETURN u, collect(DISTINCT f) as followedUsers, collect(DISTINCT l) as likedRecipes, collect(DISTINCT c) as createdRecipes
    """)
    Optional<UserNode> findUserWithAllRelations(String id);

}
