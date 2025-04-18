package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.Enumerators;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

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
    public ResponseEntity<?> getUserById(@PathVariable int id) {
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
    public ResponseEntity<?> CreateUser(@RequestBody User user) throws Exception {

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

    // Complete Update of a user
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> UpdateUser(@RequestBody User user, @PathVariable int id) throws Exception {

        user.set_id(id);
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
    public ResponseEntity<?> UpdateUser(@RequestBody Map<String, Object> params, @PathVariable int id) throws Exception {

        try {
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
    public ResponseEntity<?> DeleteUser(@PathVariable int id) throws Exception {
        try{
            userService.DeleteUser(id);
            return ResponseEntity.ok(new GenericOkMessage("User successfully deleted"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(e.getMessage()));
        }
    }
}
