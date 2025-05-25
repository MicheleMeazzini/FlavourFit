package com.example.demo.model.aggregations;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UsersPerMonth {
    @Field("_id")
    private String month;
    private Integer count;
}

