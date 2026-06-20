package com.example.scooterrent.repository;

import com.example.scooterrent.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentalId(Long rentalId);
    List<Payment> findByStatus(String status);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findByPaymentType(String paymentType);
} 