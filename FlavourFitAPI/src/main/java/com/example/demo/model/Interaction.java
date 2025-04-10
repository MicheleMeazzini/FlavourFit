package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "interaction")
public class Interaction {

    @Id
    private int _id;
    private String review;
    private int rating;
    private int user_id;
    private Date date;
    private String author;
}