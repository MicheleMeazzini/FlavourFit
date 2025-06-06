package com.example.demo.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupConnectionVerifier {

    private final MongoTemplate mongoTemplate;
    private final Driver neo4jDriver;

    public StartupConnectionVerifier(MongoTemplate mongoTemplate, Driver neo4jDriver) {
        this.mongoTemplate = mongoTemplate;
        this.neo4jDriver = neo4jDriver;
    }

    @EventListener
    public void verifyConnections(ApplicationReadyEvent event) {
        // MongoDB check
        try {
            mongoTemplate.getDb().listCollectionNames().first();
            System.out.println("✅ MongoDB connected successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to MongoDB: " + e.getMessage());
            System.exit(1);
        }

        // Neo4j check

        try (Session session = neo4jDriver.session()) {
            Result result = session.run("MATCH (n:User) RETURN COUNT(n) AS count");
            long count = result.single().get("count").asLong();
            System.out.println("✅ Neo4j connected successfully. User count: " + count);
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Neo4j: " + e.getMessage());
            System.exit(1);
        }
    }
}

