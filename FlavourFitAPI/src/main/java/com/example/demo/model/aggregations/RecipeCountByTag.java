package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class RecipeCountByTag {
    @Field("_id")
    private String tag;
    private Integer count;
}

