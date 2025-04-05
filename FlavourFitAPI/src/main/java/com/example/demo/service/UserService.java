package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import lombok.Data;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> GetAllUsers() throws Exception {
        return userRepository.findAll();
        //return new ArrayList<User>();
    }

    public Optional<User> GetUserById(int id) throws Exception {
        /*
        if(id == 2)
            throw new Exception();
        User user = new User();
        user.set_id(1);
        user.setUsername("mariorossi");
        user.setEmail("mario.rossi@example.com");
        user.setPlain_password("password123");
        user.setHashed_password("hash_simulato");
        user.setRegistration_date(new Date());
        user.setRole(false);
        return user;
        */
         return userRepository.findById(id);
    }
}
