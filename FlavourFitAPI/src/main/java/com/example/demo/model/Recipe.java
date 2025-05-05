package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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

    @Data
    public static class Ingredient
    {
        private String name;
        private String quantity;
    }
}
