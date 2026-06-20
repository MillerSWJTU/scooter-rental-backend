package com.example.scooterrent.dto;

public class ScooterStatsDTO {
    private Long id;
    private String model;
    private Integer batteryLevel;
    private String status;
    private String location;
    private Double pricePerHour;
    private Integer rentalCount;
    private Double totalRevenue;

    public ScooterStatsDTO() {}

    public ScooterStatsDTO(Long id, String model, Integer batteryLevel, String status, String location, Double pricePerHour, Integer rentalCount, Double totalRevenue) {
        this.id = id;
        this.model = model;
        this.batteryLevel = batteryLevel;
        this.status = status;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.rentalCount = rentalCount;
        this.totalRevenue = totalRevenue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }
    public Integer getRentalCount() { return rentalCount; }
    public void setRentalCount(Integer rentalCount) { this.rentalCount = rentalCount; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
} 