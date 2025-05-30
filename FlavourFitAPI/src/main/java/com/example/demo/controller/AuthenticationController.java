package com.example.demo.controller;

import com.example.demo.model.document.User;
import com.example.demo.model.authentication.AuthRequest;
import com.example.demo.model.authentication.AuthResponse;
import com.example.demo.utils.JwtUtil;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final AuthenticationService authService;

    public AuthenticationController(JwtUtil jwtUtil, AuthenticationService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Optional<User> user = authService.GetUserByUsername(authRequest.getUsername());

            if (user.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect Username");

            User userValue = user.get();
            if (!authRequest.getEncryptedPassword().equals(user.get().getHashed_password()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect Password");

            // if i'm here i have username and password correct
            String token = jwtUtil.generateToken(userValue);
            // return userId and Token
            AuthResponse response = new AuthResponse();
            response.setUserId(user.get().get_id());
            response.setToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
