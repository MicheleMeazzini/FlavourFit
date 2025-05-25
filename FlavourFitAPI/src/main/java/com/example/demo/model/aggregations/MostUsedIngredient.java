package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class MostUsedIngredient {
    @Field("_id")
    private String name;
    private Integer occurrences;
}

