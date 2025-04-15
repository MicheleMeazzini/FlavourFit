package com.example.demo.controller;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.GenericOkMessage;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.Enumerators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.GeneralPath;
import java.util.Date;
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
    @GetMapping ()
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(userService.GetAllUsers());
        }
        catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    // Read User By Id
    @GetMapping ("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.GetUserById(id));
        }
        catch (Exception e) {
            //return (List<User>) ResponseEntity.notFound().build();
            String errorMessage = "User not found";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    // Create a User
    @PostMapping ()
    public ResponseEntity<?> CreateUser(@RequestBody User user) throws Exception {

        Enumerators.UserError result = userService.SaveUser(user);
        switch(result){
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

    // Update a user
    @PutMapping ("/{id}")
    public ResponseEntity<?> UpdateUser(@RequestBody User user, @PathVariable int id) throws Exception {
        Optional<User> exist = userService.GetUserById(id);
        if(exist.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"User not found\"}");
        }
        user.set_id(id); // lo metto così uno nel body può anche non passarlo

        Enumerators.UserError result = userService.SaveUser(user);
        switch(result){
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
                return ResponseEntity.status(HttpStatus.OK).body(new GenericOkMessage("User successfully updated"));
            }
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Generic error"));
            }
        }
    }

    // Delete a user
    @DeleteMapping ("/{username}")
    public void DeleteUser(@PathVariable int username) throws Exception {
        return;
    }
}
