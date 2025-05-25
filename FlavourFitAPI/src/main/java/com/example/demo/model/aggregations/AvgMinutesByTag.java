package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class AvgMinutesByTag {
    @Field("_id")
    private String tag;
    private Double averageMinutes;
}

