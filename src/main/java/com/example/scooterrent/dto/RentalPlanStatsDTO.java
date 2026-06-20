package com.example.scooterrent.dto;

public class RentalPlanStatsDTO {
    private String plan;
    private long orderCount;
    private double totalRevenue;

    public RentalPlanStatsDTO() {}

    public RentalPlanStatsDTO(String plan, long orderCount, double totalRevenue) {
        this.plan = plan;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
