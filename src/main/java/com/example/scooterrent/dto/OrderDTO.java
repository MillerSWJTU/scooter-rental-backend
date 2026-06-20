package com.example.scooterrent.dto;

import com.example.scooterrent.enums.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private String username;
    private Long scooterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalHours;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 