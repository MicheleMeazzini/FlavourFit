package com.example.demo.model.authentication;

import lombok.Data;

@Data
public class AuthResponse {
    private Integer userId;
    private String token;
}
