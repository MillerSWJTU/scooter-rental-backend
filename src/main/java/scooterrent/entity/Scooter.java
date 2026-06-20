package scooterrent.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import scooterrent.enums.ScooterStatus;

@Data
@Entity
@Table(name = "scooters")
public class Scooter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Double pricePerHour;

    @Column(nullable = false)
    private Double pricePerFourHours;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column(nullable = false)
    private Double pricePerWeek;

    @Column(nullable = false)
    private Integer batteryLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScooterStatus status;

    private String location;
    private Double latitude;
    private Double longitude;

    private String imageUrl;

    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextMaintenanceDate;
    private String maintenanceNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 