package com.example.scooterrent.repository;

import com.example.scooterrent.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT SUM(r.totalCost) FROM Rental r")
    Double sumTotalCost();

    @Query("SELECT SUM(r.totalCost) FROM Rental r WHERE r.createdAt >= :start AND r.createdAt < :end")
    Double sumTotalCostByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Rental> findByUserUsername(String username);

    @Query("SELECT r FROM Rental r WHERE r.scooter.id = :scooterId")
    List<Rental> findByScooterId(@Param("scooterId") Long scooterId);

    @Query("SELECT r.plan, COUNT(r), SUM(r.totalCost) FROM Rental r GROUP BY r.plan")
    List<Object[]> countAndSumByPlan();

    @Query("SELECT r.scooter.id, COUNT(r), SUM(r.totalCost) FROM Rental r GROUP BY r.scooter.id")
    List<Object[]> findScooterRentalStats();

    @Query("SELECT r FROM Rental r WHERE r.startTime >= :start AND r.startTime < :end")
    List<Rental> findByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
} 