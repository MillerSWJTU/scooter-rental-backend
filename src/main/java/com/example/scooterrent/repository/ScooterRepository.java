package com.example.scooterrent.repository;

import com.example.scooterrent.entity.Scooter;
import com.example.scooterrent.enums.ScooterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByStatus(ScooterStatus status);
    List<Scooter> findByStatusAndBatteryLevelGreaterThan(ScooterStatus status, Integer batteryLevel);
    List<Scooter> findByLocation(String location);
    List<Scooter> findByStatus(String status);
} 