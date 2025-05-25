package com.example.demo.model.aggregations;

import lombok.Data;

@Data
public class RecipeWithIngredientCount {
    private String name;
    private Integer ingredientCount;
}

