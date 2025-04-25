package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "recipe")
public class Recipe
{

    @Id
    private String _id;

    private String name;
    private int minutes;

    private List<Ingredient> ingredient_embedded;
    private List<String> tags;
    private List<Double> nutrition;
    private List<String> steps;
    private String description;
    private List<Integer> interactions;

    public static class Ingredient 
    {
        private String name;
        private String quantity;

    }
}
