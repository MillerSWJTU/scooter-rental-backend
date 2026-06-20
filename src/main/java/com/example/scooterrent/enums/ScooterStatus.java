package com.example.scooterrent.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ScooterStatus {
    AVAILABLE("可用"),
    RENTED("已租出"),
    MAINTENANCE("维护中"),
    OFFLINE("离线/禁用");

    private final String description;

    ScooterStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 