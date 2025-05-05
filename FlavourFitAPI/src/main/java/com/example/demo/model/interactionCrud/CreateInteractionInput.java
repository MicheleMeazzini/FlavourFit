package com.example.demo.model.interactionCrud;

import lombok.Data;

@Data
public class CreateInteractionInput {

    private String review;
    private int rating;
    private String author;
    private String recipe_id;
}
