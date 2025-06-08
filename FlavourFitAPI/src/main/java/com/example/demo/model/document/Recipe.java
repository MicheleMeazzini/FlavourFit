package com.example.demo.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "recipe")
public class Recipe
{

    @Id
    private String _id;

    private String name;
    private int minutes;

    private List<Ingredient> ingredient;
    private List<String> tags;
    private List<Double> nutrition;
    private List<String> steps;
    private String description;
    private List<String> interactions;
    private String author_id;
    @Field("author_name")
    private String author;
    private Date date;

    @Data
    public static class Ingredient
    {
        private String name;
        private String quantity;
    }
}
