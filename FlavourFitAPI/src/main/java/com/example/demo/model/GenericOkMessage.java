package com.example.demo.model;

import lombok.Data;

@Data
public class GenericOkMessage {
    private String message;
    public GenericOkMessage(String message) {
        this.message = message;
    }
}
