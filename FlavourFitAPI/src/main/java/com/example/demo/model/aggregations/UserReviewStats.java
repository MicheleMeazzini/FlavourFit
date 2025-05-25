package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UserReviewStats {
    @Field("_id")
    private String author; // Può essere ObjectId o String, a seconda del campo
    private Integer totaleReview;
    private Double mediaValutazioneData;
}
