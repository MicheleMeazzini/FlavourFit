package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getStackTrace());
        }
    }

    // Create a User
    @PostMapping ()
    public void CreateUser(@RequestBody User utente) {
        return;
    }

    // Update a user
    @PutMapping ()
    public void UpdateUser() {
        return;
    }

    // Delete a user
    @DeleteMapping ("/{id}")
    public void DeleteUser(@PathVariable int id) {
        return;
    }
}
