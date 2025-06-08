package com.example.demo.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "interaction")
public class Interaction {
    @Id
    private String _id;
    private String review;
    private int rating;
    private Date date;
    private String author;
}