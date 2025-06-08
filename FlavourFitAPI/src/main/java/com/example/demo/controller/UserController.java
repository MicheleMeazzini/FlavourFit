package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.document.User;
import com.example.demo.model.graph.UserNode;
import com.example.demo.repository.document.UserRepository;
import com.example.demo.repository.graph.UserNodeRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.AuthorizationUtil;
import com.example.demo.utils.Enumerators;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;
    private final AuthorizationUtil authorizationUtil;

    public UserController(UserService userService, UserRepository userRepository, UserNodeRepository userNodeRepository, AuthorizationUtil authorizationUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
        this.authorizationUtil = authorizationUtil;
    }


   /* @GetMapping("/check-role")
    public ResponseEntity<String> checkRole(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("userRole");
        boolean isAdmin = role != null && role == 1;

        return ResponseEntity.ok(isAdmin ? "Sei un admin" : "Utente normale");
    }*/

    // Read All Users
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(userService.GetAllUsers());
        } catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    // Read User By Id
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.GetUserById(id));
        } catch (Exception e) {
            //return (List<User>) ResponseEntity.notFound().build();
            String errorMessage = "User not found";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    // Create a User
    @PostMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> CreateUser(@RequestBody User user) {
        try {
            Enumerators.UserError result = userService.AddUser(user);
            switch (result) {
                case MISSING_PASSWORD -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Password is needed"));
                }
                case MISSING_EMAIL -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email is needed"));
                }
                case MISSING_USERNAME -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Username is needed"));
                }
                case DUPLICATE_EMAIL -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email is already in use"));
                }
                case DUPLICATE_USERNAME -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Username is already in use"));
                }
                case NO_ERROR -> {
                    return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("User successfully created"));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
                }
            }
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Internal server error: " + e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage("Internal Server Error"));
        }
    }

    // Complete Update of a user
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> UpdateUser(@RequestBody User user, @PathVariable String id, HttpServletRequest request) throws Exception {

        user.set_id(id);
        boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, id);
        if (!authorized) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("No access to update user"));
        }
        Enumerators.UserError result = userService.UpdateUser(user);
        switch (result) {
            case MISSING_PASSWORD -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Password is needed"));
            }
            case MISSING_EMAIL -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email is needed"));
            }
            case MISSING_USERNAME -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Username is needed"));
            }
            case DUPLICATE_EMAIL -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email is already in use"));
            }
            case DUPLICATE_USERNAME -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Username is already in use"));
            }
            case USER_NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("User not found"));
            }
            case NO_ERROR -> {
                return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("User successfully updated"));
            }
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
            }
        }
    }

    @PatchMapping ("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> UpdateUser(@RequestBody Map<String, Object> params, @PathVariable String id, HttpServletRequest request) throws Exception {

        try {
            boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, id);
            if (!authorized) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorMessage("No access to update user"));
            }

            Enumerators.UserError result = userService.UpdateUser(id, params);
            switch (result) {
                case DUPLICATE_EMAIL -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Email is already in use"));
                }
                case DUPLICATE_USERNAME -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Username is already in use"));
                }
                case NO_ERROR -> {
                    return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("User successfully updated"));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
                }
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage("Internal Server Error"));
        }
    }

    // Delete a user
    @DeleteMapping ("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(@PathVariable String id, HttpServletRequest request) throws Exception{
        try{

            boolean authorized = authorizationUtil.verifyOwnershipOrAdmin(request, id);
            if(!authorized)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("No access to delete user"));

            userService.DeleteUser(id);
            return ResponseEntity.ok(new GenericOkMessage("User successfully deleted"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(e.getMessage()));
        }
    }

    // Read All Users GraphDB
    @GetMapping("/node")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUsersNodes() {
        try {
            return ResponseEntity.ok(userNodeRepository.findAll());
        } catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    // Read All Users
    @GetMapping("/node/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUsersNodesById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userNodeRepository.findUserNodeById(id));
        } catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @PostMapping("/{followerId}/follow/{followeeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> followUser(@PathVariable String followerId, @PathVariable String followeeId) {
        try {
            userService.followUser(followerId, followeeId);
            return ResponseEntity.ok(new GenericOkMessage("Follow relationship created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Error creating follow relationship: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> unfollowUser(@PathVariable String followerId, @PathVariable String followeeId) {
        try {
            userService.unfollowUser(followerId, followeeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Unfollow failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{userId}/like/{recipeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> likeRecipe(@PathVariable String recipeId, @PathVariable String userId) {
        try {
            userService.likeRecipe(userId, recipeId);
            return ResponseEntity.ok().body("Recipe liked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to like recipe: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/unlike/{recipeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> unlikeRecipe(@PathVariable String recipeId, @PathVariable String userId) {
        try {
            userService.unlikeRecipe(userId, recipeId);
            return ResponseEntity.ok().body("Recipe unliked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to unlike recipe: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/suggested-follows")
    @SecurityRequirement(name = "bearerAuth")
    public List<UserNode> suggestFollows(@PathVariable String userId) {
        return userService.suggestUsersToFollow(userId);
    }

    @GetMapping("/most-followed")
    @SecurityRequirement(name = "bearerAuth")
    public List<UserNode> getMostFollowedUsers() {
        return userService.getMostFollowedUsers();
    }
}
