package com.example.scooterrent.dto;

import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private String username;
    private Long paymentId;
    private String type;
    private String content;
    private Integer rating;
    private String contactInfo;
} 