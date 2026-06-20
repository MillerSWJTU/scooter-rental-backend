package scooterrent.dto;

import lombok.Data;

@Data
public class ScooterPriceUpdateRequest {
    private Double pricePerHour;
    private Double pricePerFourHours;
    private Double pricePerDay;
    private Double pricePerWeek;
} 