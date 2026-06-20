package com.example.scooterrent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Integer id;
    private String username;
    private Long paymentId;
    private String type;
    private String content;
    private String status;
    private String adminResponse;
    private Integer rating;
    private String contactInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
} 