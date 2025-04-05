package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

@Service
public class UserService {
    public List<User> GetAllUsers() throws Exception {
        throw new Exception();
        //return new ArrayList<User>();
    }

    public User GetUserById(int id) throws Exception {
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
    }
}
