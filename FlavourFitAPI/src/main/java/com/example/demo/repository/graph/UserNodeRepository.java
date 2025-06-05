package com.example.demo.repository.graph;

import com.example.demo.model.graph.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;


@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, String> {
    @Query("MATCH (u:User {id: $id}) DETACH DELETE u")
    void deleteUserNodeAndRelationships(String id);

}
