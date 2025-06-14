package com.example.demo.service;

import com.example.demo.model.document.Interaction;
import com.example.demo.model.document.Recipe;
import com.example.demo.model.document.User;
import com.example.demo.model.graph.RecipeNode;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.InteractionRepository;
import com.example.demo.repository.document.RecipeRepository;
import com.example.demo.repository.document.UserRepository;
import com.example.demo.repository.graph.RecipeNodeRepository;
import com.example.demo.repository.graph.UserNodeRepository;
import com.example.demo.utils.Enumerators;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;
    private final RecipeNodeRepository recipeNodeRepository;
    private final Neo4jService neoj4Service;
    private final UserServiceTransactional userServiceTransactional;


    public UserService(UserRepository userRepository, UserNodeRepository userNodeRepository,
                       Neo4jService neo4jService, RecipeNodeRepository recipeNodeRepository, UserServiceTransactional userServiceTransactional) {
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
        this.recipeNodeRepository = recipeNodeRepository;
        this.neoj4Service = neo4jService;
        this.userServiceTransactional = userServiceTransactional;
    }

    // <editor-fold desc="Get User Operations">
    public List<User> GetAllUsers() throws Exception {
        return userRepository.findAll();
        //return new ArrayList<User>();
    }

    public Optional<User> GetUserById(String id) throws Exception {
         return userRepository.findById(id);
    }

    public List<UserNode> searchUserNodesByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return userNodeRepository.findUserNodesByPartialName(partialName);
    }

    public Optional<User> GetUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }
    // </editor-fold>

    // <editor-fold desc="Create User Operations">

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

            // MongoDB: save user
            User savedUser = userRepository.save(user);

            // Neo4j: create node
            UserNode userNode = new UserNode();
            userNode.setId(savedUser.get_id());
            userNode.setName(savedUser.getUsername());
            try {
                userNodeRepository.save(userNode);
            }
            catch (Exception e) {
                // Rollback MongoDB in caso di errore
                userRepository.delete(savedUser);
                throw new RuntimeException("Failed to create user in Neo4j, rollback Mongo " + e.getMessage());
            }

            return Enumerators.UserError.NO_ERROR;


    }

    // </editor-fold>

    // <editor-fold desc="Update User Operations">
    public Enumerators.UserError UpdateUser(User user) throws Exception {
        Optional<User> exist = this.GetUserById(user.get_id());
        if(exist.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }

        boolean neo4jUpdateNeeded = !user.getUsername().equals(exist.get().getUsername());

        if(!user.getEmail().equals(exist.get().getEmail()) && userRepository.existsByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(!user.getUsername().equals(exist.get().getUsername()) && userRepository.existsByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        UserNode backupNode = neoj4Service.findUserWithAllRelationships(user.get_id())
                .orElseThrow(() -> new Exception("User not found in Neo4j"));

        if (neo4jUpdateNeeded) {
            UserNode newNode = new UserNode();
            newNode.setId(user.get_id());
            newNode.setName(user.getUsername());
            userNodeRepository.save(newNode);
        }
        try {
            userServiceTransactional.updateUserInMongoDb(user, exist.get().getUsername());
        } catch (Exception e) {
            // Rollback in caso di errore
            if(neo4jUpdateNeeded)
                userNodeRepository.save(backupNode);
            throw new Exception("Failed to update user in MongoDB: " + e.getMessage());
        }
        return Enumerators.UserError.NO_ERROR;
    }

    public Enumerators.UserError UpdateUser(String id, Map<String, Object> params) throws Exception {
        Optional<User> user = this.GetUserById(id);

        if(user.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }
        String oldUsername = user.get().getUsername();
        User usr = user.get();
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
                    usr.setUsername(newUsername);
                    neo4jUpdateNeeded = true;
                    break;

                case "email":
                    String newEmail = (String) fieldValue;
                    if (userRepository.existsByEmail(newEmail)) {
                        return Enumerators.UserError.DUPLICATE_EMAIL;
                    }
                    usr.setEmail(newEmail);
                    break;

                case "hashed_password":
                    usr.setHashed_password((String) fieldValue);
                    break;

                case "role":
                    if (fieldValue instanceof Number) {
                        usr.setRole(((Number) fieldValue).intValue());
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Not Updatable field: " + fieldName);
            }
        }

        UserNode backupNode = neoj4Service.findUserWithAllRelationships(id)
                .orElseThrow(() -> new Exception("User not found in Neo4j"));

        if (neo4jUpdateNeeded) {
            UserNode newNode = new UserNode();
            newNode.setId(usr.get_id());
            newNode.setName(usr.getUsername());
            userNodeRepository.save(newNode);
        }
        try {
            userServiceTransactional.updateUserInMongoDb(usr, oldUsername);
        } catch (Exception e) {
            // Rollback in caso di errore
            if(neo4jUpdateNeeded)
                userNodeRepository.save(backupNode);
            throw new Exception("Failed to update user in MongoDB: " + e.getMessage());
        }
        return Enumerators.UserError.NO_ERROR;
    }

    // </editor-fold>

    // <editor-fold desc="Delete User Operations">

    public boolean deleteFromNeo4j(String id) throws Exception {
        Optional<UserNode> userNode = userNodeRepository.findUserNodeById(id);
        if(userNode.isEmpty()){
            return false;
        }
        try {
            userNodeRepository.deleteUserNodeAndRelationships(id);
        }
        catch (Exception e) {
            System.out.println("Error deleting UserNode from Neo4j: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void DeleteUser(String id) throws Exception {

        UserNode backupNode = neoj4Service.findUserWithAllRelationships(id)
                .orElseThrow(() -> new Exception("User not found in Neo4j"));

        boolean deletedFromNeo4j = deleteFromNeo4j(id);
        if(!deletedFromNeo4j) {
            throw new Exception("Failed to delete user from Neo4j");

        }
        try {
            userServiceTransactional.deleteUserFromMongoDb(id);
        }
        catch(Exception e)
        {
            // rollback in caso di errore
            userNodeRepository.save(backupNode);
            throw new Exception("Failed to delete user from MongoDB: " + e.getMessage());
        }

    }

    // </editor-fold>

    //<editor-fold desc="Graph Operations">

    public void likeRecipe(String userId, String recipeId) throws Exception {
        if (userNodeRepository.findUserNodeById(userId).isEmpty()) {
            throw new Exception("UserNode not found");
        }
        if (recipeNodeRepository.findRecipeNodeById(recipeId).isEmpty()) {
            throw new Exception("Recipe not found in MongoDB");
        }


        userNodeRepository.likeRecipe(userId, recipeId);
    }

    public void unlikeRecipe(String userId, String recipeId) throws Exception {
        if (userNodeRepository.findUserNodeById(userId).isEmpty()) {
            throw new Exception("UserNode not found");
        }
        if (recipeNodeRepository.findRecipeNodeById(recipeId).isEmpty()) {
            throw new Exception("Recipe not found");
        }

        userNodeRepository.unlikeRecipe(userId, recipeId);
    }


    @Transactional
    public void followUser(String followerId, String followeeId) throws Exception {
        if (userNodeRepository.findUserNodeById(followerId).isEmpty()) {
            throw new Exception("Follower node not found");
        }
        if (userNodeRepository.findUserNodeById(followeeId).isEmpty()) {
            throw new Exception("Followee node not found");
        }

        userNodeRepository.addFollowRelationship(followerId, followeeId);
    }

    public void unfollowUser(String followerId, String followeeId) throws Exception {
        Optional<UserNode> follower = userNodeRepository.findUserNodeById(followerId);
        Optional<UserNode> followee = userNodeRepository.findUserNodeById(followeeId);

        if (follower.isEmpty() || followee.isEmpty()) {
            throw new Exception("User not found");
        }

        userNodeRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserNode> suggestUsersToFollow(String userId) {
        return userNodeRepository.suggestUsersToFollow(userId);
    }

    public List<UserNode> getMostFollowedUsers() {
        return userNodeRepository.findMostFollowedUsers();
    }


    // </editor-fold>
}

@Component
class UserServiceTransactional {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final InteractionRepository interactionRepository;

    UserServiceTransactional(UserRepository userRepository, RecipeRepository recipeRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.interactionRepository = interactionRepository;
    }
    //<editor-fold desc="Mongo User Transactional operations">

    @Transactional("transactionManager")
    public void deleteUserFromMongoDb(String id) throws Exception {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        String username = user.getUsername();

        List<Recipe> recipes = recipeRepository.getRecipeByAuthor(username);

        List<Interaction> recipeInteractions = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<String> interactionIds = recipe.getInteractions();
            /*
            for(String interaction : interactionIds) {
                Optional<Interaction> interactionOpt = interactionRepository.findById(interaction);
                interactionOpt.ifPresent(recipeInteractions::add);
            }
            */
             recipeInteractions.addAll(
                    interactionRepository.findAllById(recipe.getInteractions()));
        }
        ;
        List<Interaction> interactions = interactionRepository.getInteractionByAuthor(username);
        interactionRepository.deleteAll(interactions);
        interactionRepository.deleteAll(recipeInteractions);
        recipeRepository.deleteAll(recipes);

        // Simula timeout/errore
        // Thread.sleep(70000); // oppure:
        //if(true) throw new RuntimeException("Simulated error during user deletion");
        userRepository.deleteById(id);

    }

    @Transactional("transactionManager")
    public void updateUserInMongoDb(User updatedUser, String oldUsername) {

        List<Recipe> recipes = recipeRepository.getRecipeByAuthor(oldUsername);
        for (Recipe recipe : recipes) {
            recipe.setAuthor(updatedUser.getUsername());
            recipeRepository.save(recipe);
        }

        List<Interaction> interactions = interactionRepository.getInteractionByAuthor(oldUsername);
        for (Interaction interaction : interactions) {
            interaction.setAuthor(updatedUser.getUsername());
            interactionRepository.save(interaction);
        }
        //if(true) throw new RuntimeException("Simulated error during user deletion");
        userRepository.save(updatedUser);
    }

    //</editor-fold>
}


