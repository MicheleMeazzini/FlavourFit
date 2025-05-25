package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class RatingDistribution {
    @Field("_id")
    private Integer rating;
    private Integer count;
}
