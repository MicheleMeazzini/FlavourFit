package com.example.demo.service;

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

    public Optional<User> GetUserById(String id) throws Exception {
         return userRepository.findById(id);
    }

    public Optional<User> GetUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }

    // Create & Update Function
    public Enumerators.UserError AddUser(User user) throws Exception {
        if(user.getUsername() == null || user.getUsername().isEmpty())
            return Enumerators.UserError.MISSING_USERNAME;

        if(user.getEmail() == null || user.getEmail().isEmpty()) // se si vuole controllare che sia una mail effettivamente
            return Enumerators.UserError.MISSING_EMAIL;

        if(user.getHashed_password() == null || user.getHashed_password().isEmpty())
            return Enumerators.UserError.MISSING_PASSWORD;

        if(userRepository.existsByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(userRepository.existsByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        user.setRegistration_date(new Date());
        try {
            userRepository.save(user);
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            return Enumerators.UserError.GENERIC_ERROR;
        }
    }

    public Enumerators.UserError UpdateUser(User user) throws Exception {
        Optional<User> exist = this.GetUserById(user.get_id());
        if(exist.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }

        if(!user.getEmail().equals(exist.get().getEmail()) && userRepository.existsByEmail(user.getEmail()))
            return Enumerators.UserError.DUPLICATE_EMAIL;

        if(!user.getUsername().equals(exist.get().getUsername()) && userRepository.existsByUsername(user.getUsername()))
            return Enumerators.UserError.DUPLICATE_USERNAME;

        try {
            userRepository.save(user);
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
            return Enumerators.UserError.GENERIC_ERROR;
        }
    }

    public Enumerators.UserError UpdateUser(String id, Map<String, Object> params) throws Exception {
        Optional<User> user = this.GetUserById(id);
        if(user.isEmpty()){
            return Enumerators.UserError.USER_NOT_FOUND;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {
                case "username":
                    String newUsername = (String) fieldValue;
                    if (userRepository.existsByUsername(newUsername)) {
                        return Enumerators.UserError.DUPLICATE_USERNAME;
                    }
                    user.get().setUsername(newUsername);
                    break;

                case "email":
                    String newEmail = (String) fieldValue;
                    if (userRepository.existsByEmail(newEmail)) {
                        return Enumerators.UserError.DUPLICATE_EMAIL;
                    }
                    user.get().setEmail(newEmail);
                    break;

                case "hashed_password":
                    user.get().setHashed_password((String) fieldValue);
                    break;

                case "plain_password":
                    user.get().setPlain_password((String) fieldValue);
                    break;

                case "role":
                    if (fieldValue instanceof Number) {
                        user.get().setRole(((Number) fieldValue).intValue());
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Campo non aggiornabile: " + fieldName);
            }
        }

        try {
            userRepository.save(user.get());
            return Enumerators.UserError.NO_ERROR;
        }
        catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
            return Enumerators.UserError.GENERIC_ERROR;
        }
    }

    // Delete function
    public void DeleteUser(String id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new Exception("User not found");
        }
        userRepository.delete(user.get());
    }
}
