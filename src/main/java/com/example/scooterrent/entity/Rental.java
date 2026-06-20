package com.example.scooterrent.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scooter_id", nullable = false)
    private Scooter scooter;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private String startLocation;

    private String endLocation;

    @Column(nullable = false)
    private Boolean active;

    private Double totalCost;

    private String notes;

    @Column(nullable = false)
    private Integer duration; // 租赁时长（小时）

    @Column(nullable = false)
    private String plan; // 租赁方案：DAILY, WEEKLY, FOUR_HOURS, HOURLY

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return active;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        if (active) {
            return "ACTIVE";
        } else if (totalCost == 0) {
            return "CANCELLED";
        } else if (endTime != null) {
            return "COMPLETED";
        } else {
            return "UNKNOWN";
        }
    }

    public void setStatus(String status) {
        switch (status.toUpperCase()) {
            case "ACTIVE":
                this.active = true;
                break;
            case "COMPLETED":
                this.active = false;
                this.endTime = LocalDateTime.now();
                break;
            case "CANCELLED":
                this.active = false;
                this.totalCost = 0.0;
                break;
            default:
                this.active = false;
        }
    }
} 