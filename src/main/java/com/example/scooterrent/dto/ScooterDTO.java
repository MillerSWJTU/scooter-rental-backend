package com.example.scooterrent.dto;

import com.example.scooterrent.enums.ScooterStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScooterDTO {
    private Long id;
    private String model;
    private ScooterStatus status;
    private Double pricePerHour;
    private Double pricePerFourHours;
    private Double pricePerDay;
    private Double pricePerWeek;
    private Integer batteryLevel;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String lastMaintenanceDate;
    private String nextMaintenanceDate;
    private String maintenanceNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 