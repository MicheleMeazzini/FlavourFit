package com.example.demo.model.aggregations;

import lombok.Data;

@Data
public class RecipeAverageRating {
    private String id;
    private Double averageRating;
    private Integer reviewCount;
}
