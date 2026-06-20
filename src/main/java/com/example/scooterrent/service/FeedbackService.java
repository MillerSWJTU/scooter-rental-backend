package com.example.scooterrent.service;

import com.example.scooterrent.dto.FeedbackDTO;
import com.example.scooterrent.dto.FeedbackRequestDTO;
import com.example.scooterrent.entity.Feedback;
import com.example.scooterrent.entity.Payment;
import com.example.scooterrent.entity.User;
import com.example.scooterrent.enums.FeedbackStatus;
import com.example.scooterrent.enums.FeedbackType;
import com.example.scooterrent.repository.FeedbackRepository;
import com.example.scooterrent.repository.PaymentRepository;
import com.example.scooterrent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public FeedbackDTO createFeedback(FeedbackRequestDTO requestDTO) {
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setContent(requestDTO.getContent());
        feedback.setType(FeedbackType.valueOf(requestDTO.getType()));
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setRating(requestDTO.getRating());
        feedback.setContactInfo(requestDTO.getContactInfo());

        if (requestDTO.getPaymentId() != null) {
            Payment payment = paymentRepository.findById(requestDTO.getPaymentId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            feedback.setPayment(payment);
        }

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    @Transactional
    public FeedbackDTO createFeedbackForPayment(Long paymentId, FeedbackRequestDTO requestDTO) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setPayment(payment);
        feedback.setContent(requestDTO.getContent());
        feedback.setType(FeedbackType.valueOf(requestDTO.getType()));
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setRating(requestDTO.getRating());
        feedback.setContactInfo(requestDTO.getContactInfo());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    public FeedbackDTO getFeedbackById(Integer id) {
        return convertToDTO(feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found")));
    }

    public List<FeedbackDTO> getFeedbacksByUsername(String username) {
        return feedbackRepository.findByUserUsername(username)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> getFeedbacksByStatus(FeedbackStatus status) {
        return feedbackRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> getFeedbacksByType(String type) {
        return feedbackRepository.findByType(FeedbackType.valueOf(type))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackDTO updateFeedbackStatus(Integer id, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setStatus(status);
        if (status == FeedbackStatus.RESOLVED) {
            feedback.setResolvedAt(LocalDateTime.now());
        }

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(updatedFeedback);
    }

    @Transactional
    public FeedbackDTO addAdminResponse(Integer id, String response) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setAdminResponse(response);
        feedback.setStatus(FeedbackStatus.RESOLVED);
        feedback.setResolvedAt(LocalDateTime.now());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(updatedFeedback);
    }

    @Transactional
    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteById(id);
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setContent(feedback.getContent());
        dto.setType(feedback.getType().name());
        dto.setStatus(feedback.getStatus().name());
        dto.setRating(feedback.getRating());
        dto.setContactInfo(feedback.getContactInfo());
        dto.setAdminResponse(feedback.getAdminResponse());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setResolvedAt(feedback.getResolvedAt());
        
        // 设置用户名
        if (feedback.getUser() != null) {
            dto.setUsername(feedback.getUser().getUsername());
        } else {
            dto.setUsername("Unknown User");
        }
        
        return dto;
    }
} 