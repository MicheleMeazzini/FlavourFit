package com.example.demo.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "user")

public class User {

    @Id
    private String _id;
    private String username;
    private String email;
    private String hashed_password;
    private String plain_password;
    private Date registration_date;
    private int role;

}
