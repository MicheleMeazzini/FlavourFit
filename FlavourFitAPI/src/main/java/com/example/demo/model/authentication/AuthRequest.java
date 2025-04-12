package com.example.demo.model.authentication;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String encryptedPassword;
}
