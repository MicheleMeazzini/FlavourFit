package com.example.demo.model.authentication;

import lombok.Data;

@Data
public class AuthResponse {
    private String userId;
    private String token;
}
