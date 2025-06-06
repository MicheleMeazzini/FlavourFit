package com.example.demo.utils;

import com.example.demo.model.document.User;
import com.example.demo.repository.document.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
@Component
public class AuthorizationUtil {

    private final UserRepository userRepository;

    public AuthorizationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean verifyOwnershipOrAdmin(HttpServletRequest request, String inputId) {
        Integer role = (Integer) request.getAttribute("requestRole");
        String resourceOwnerId = (String) request.getAttribute("requesterId");

        return !(role == null || (role == 0 && !resourceOwnerId.equals(inputId)));        }
    }
