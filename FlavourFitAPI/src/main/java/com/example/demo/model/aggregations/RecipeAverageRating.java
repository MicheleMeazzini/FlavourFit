package com.example.demo.model.aggregations;

import lombok.Data;

@Data
public class RecipeAverageRating {
    private String id; // corresponds to _id (recipe name)
    private Double averageRating;
    private Integer reviewCount;

}
