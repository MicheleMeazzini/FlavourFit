package com.example.demo.service;

import com.example.demo.model.document.User;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.document.UserRepository;
import com.example.demo.repository.graph.UserNodeRepository;
import com.example.demo.utils.Enumerators;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;
    private final RecipeRepository recipeRepository;
    private final InteractionRepository interactionRepository;

    public UserService(UserRepository userRepository, UserNodeRepository userNodeRepository, RecipeRepository recipeRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
        this.recipeRepository = recipeRepository;
        this.interactionRepository = interactionRepository;
    }

    // <editor-fold desc="Get User Operations">
    public List<User> GetAllUsers() throws Exception {
        return userRepository.findAll();
        //return new ArrayList<User>();
    }

    public Optional<User> GetUserById(String id) throws Exception {
         return userRepository.findById(id);
    }

    public Optional<User> GetUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }
    // </editor-fold>

    // <editor-fold desc="Create User Operations">
    @Transactional
    public Enumerators.UserError AddUser(User user) throws Exception {
        if(user.getUsername() == null || user.getUsername().isEmpty())
            return Enumerators.UserError.MISSING_USERNAME;

        if(user.getEmail() == null || user.getEmail().isEmpty()) // se si vuole controllare che sia una mail effettivamente
            return Enumerators.UserError.MISSING_EMAIL;

        if(user.getHashed_password() == null || user.getHashed_password().isEmpty())
            return Enumerators.UserError.MISSING_PASSWORD;

        if(userRepository.existsByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(userRepository.existsByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        user.setRegistration_date(new Date());
        try {
            // MongoDB: save user
            User savedUser = userRepository.save(user);

            // Neo4j: create node
            UserNode userNode = new UserNode();
            userNode.setId(savedUser.get_id());
            userNode.setName(savedUser.getUsername());
            userNodeRepository.save(userNode);

            return Enumerators.UserError.NO_ERROR;

        } catch (Exception e) {
            throw new RuntimeException("User creation failed, rollback MongoDB", e);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Update User Operations">
    @Transactional
    public Enumerators.UserError UpdateUser(User user) throws Exception {
        Optional<User> exist = this.GetUserById(user.get_id());
        if(exist.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }

        if(!user.getEmail().equals(exist.get().getEmail()) && userRepository.existsByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(!user.getUsername().equals(exist.get().getUsername()) && userRepository.existsByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        try {
            userRepository.save(user);
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
            return Enumerators.UserError.GENERIC_ERROR;
        }
    }

    @Transactional
    public Enumerators.UserError UpdateUser(String id, Map<String, Object> params) throws Exception {
        Optional<User> user = this.GetUserById(id);
        if(user.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }

        boolean neo4jUpdateNeeded = false;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {
                case "username":
                    String newUsername = (String) fieldValue;
                    if (userRepository.existsByUsername(newUsername)) {
                        return Enumerators.UserError.DUPLICATE_USERNAME;
                    }
                    user.get().setUsername(newUsername);
                    neo4jUpdateNeeded = true;
                    break;

                case "email":
                    String newEmail = (String) fieldValue;
                    if (userRepository.existsByEmail(newEmail)) {
                        return Enumerators.UserError.DUPLICATE_EMAIL;
                    }
                    user.get().setEmail(newEmail);
                    break;

                case "hashed_password":
                    user.get().setHashed_password((String) fieldValue);
                    break;

                case "plain_password":
                    user.get().setPlain_password((String) fieldValue);
                    break;

                case "role":
                    if (fieldValue instanceof Number) {
                        user.get().setRole(((Number) fieldValue).intValue());
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Not Updatable field: " + fieldName);
            }
        }

        try {
            userRepository.save(user.get());

            if(neo4jUpdateNeeded){
                // Update Neo4j node
                UserNode userNode = userNodeRepository.findById(id).orElseThrow(() -> new Exception("UserNode not found"));
                userNode.setName(user.get().getUsername());
                userNodeRepository.save(userNode);
            }
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            throw new RuntimeException("Update failed: rollback Mongo", e);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Delete User Operations">
    @Transactional
    public void DeleteUser(String id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new Exception("User not found");
        }
        try {
            User usr = user.get();
            recipeRepository.deleteByAuthor(usr.getUsername()); // delete all recipes by author
            interactionRepository.deleteByAuthor(usr.getUsername()); // delete all interactions by author
            userRepository.delete(usr); // delete user from MongoDB
            userNodeRepository.deleteUserNodeAndRelationships(id); // delete user node and relationships from Neo4j
        }
        catch (Exception e) {
            throw new RuntimeException("Delete failed: rollback Mongo", e);
        }
    }
    // </editor-fold>
}
