package com.example.scooterrent.repository;

import com.example.scooterrent.entity.Order;
import com.example.scooterrent.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.username = :username")
    List<Order> findByUsername(@Param("username") String username);
    
    List<Order> findByScooterId(Long scooterId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Order o WHERE o.user.username = :username AND o.startTime BETWEEN :start AND :end")
    List<Order> findByUsernameAndStartTimeBetween(
        @Param("username") String username, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT o FROM Order o WHERE o.user.username = :username AND o.status = :status")
    List<Order> findByUsernameAndStatus(
        @Param("username") String username, 
        @Param("status") OrderStatus status
    );
} 