package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UserReviewStats {
    @Field("_id")
    private String author;
    private Integer reviewCount;
    private Double avgRatingGiven;
}
