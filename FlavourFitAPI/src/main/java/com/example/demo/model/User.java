package com.example.demo.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "user")
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _id;
    private String username;
    private String email;
    private String hashed_password;
    private String plain_password;
    private Date registration_date;
    private int role;


}
