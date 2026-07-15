package scooterrent.repository;

import scooterrent.entity.Payment;
import scooterrent.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentalId(Long rentalId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);
} 