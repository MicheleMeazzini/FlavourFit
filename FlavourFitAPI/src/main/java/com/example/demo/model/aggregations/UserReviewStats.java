package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UserReviewStats {
    @Field("_id")
    private String author;
    @Field("totale_review")
    private Integer reviewCount;
    @Field("media_valutazione_data")
    private Double avgRatingGiven;
}
