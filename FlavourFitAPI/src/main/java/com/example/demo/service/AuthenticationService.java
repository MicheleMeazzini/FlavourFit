package com.example.demo.service;

import com.example.demo.model.document.User;
import com.example.demo.repository.document.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {


    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> GetUserByUsername(String username) throws Exception {

        return userRepository.findByUsername(username);

    }
}
