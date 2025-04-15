package com.example.demo.service;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Enumerators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get Functions
    public List<User> GetAllUsers() throws Exception {
        return userRepository.findAll();
        //return new ArrayList<User>();
    }

    public Optional<User> GetUserById(int id) throws Exception {
         return userRepository.findById(id);
    }

    public Optional<User> GetUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }

    // Exists Function
    public boolean ExistByUsername(String username) throws Exception {
        return userRepository.existsByUsername(username);
    }

    public boolean ExistByEmail(String email) throws Exception {
        return userRepository.existsByUsername(email);
    }

    // Create & Update Function

    public Enumerators.UserError SaveUser(User user) throws Exception {
        if(user.getUsername() == null || user.getUsername().isEmpty())
            return Enumerators.UserError.MISSING_USERNAME;

        if(user.getEmail() == null || user.getEmail().isEmpty()) // se si vuole controllare che sia una mail effettivamente
            return Enumerators.UserError.MISSING_EMAIL;

        if(user.getHashed_password() == null || user.getHashed_password().isEmpty())
            return Enumerators.UserError.MISSING_PASSWORD;

        if(this.ExistByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(this.ExistByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        user.setRegistration_date(new Date());
        if(user.getRole() == 0) // = 0 -> not specified in the body
            user.setRole(1); // default user
        try {
            userRepository.save(user);
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
            e.printStackTrace();
            return Enumerators.UserError.GENERIC_ERROR;
        }
    }
}
