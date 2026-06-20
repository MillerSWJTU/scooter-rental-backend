package com.example.scooterrent.repository;

import com.example.scooterrent.entity.Feedback;
import com.example.scooterrent.enums.FeedbackStatus;
import com.example.scooterrent.enums.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByUserUsername(String username);
    List<Feedback> findByStatus(FeedbackStatus status);
    List<Feedback> findByType(FeedbackType type);
    List<Feedback> findByPaymentId(Long paymentId);
} 