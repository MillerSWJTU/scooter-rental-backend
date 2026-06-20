package com.example.scooterrent.controller;

import com.example.scooterrent.dto.PaymentRequest;
import com.example.scooterrent.entity.Payment;
import com.example.scooterrent.service.RentalPaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private RentalPaymentService rentalPaymentService;

    @Autowired
    private JavaMailSender mailSender; // 使用 Spring Boot 提供的 JavaMailSender

    private void sendEmail(String to, String subject, String body, Long paymentId) {
        try {
            String feedbackUrl = String.format("http://localhost:3000/feedback-form?paymentId=%d", paymentId);
            String fullBody = body + "\n\n请点击以下链接填写您的反馈意见（匿名反馈）：\n" + feedbackUrl;
    
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(fullBody);
            message.setFrom("zzp1179946058@163.com"); // 发件人邮箱
            mailSender.send(message);
            logger.info("邮件已成功发送至 {}", to);
        } catch (Exception e) {
            logger.error("发送邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败，请检查配置", e);
        }
    }

    @PostMapping("/rental/{rentalId}")
    public ResponseEntity<?> createPayment(
            @PathVariable Long rentalId,
            @RequestBody PaymentRequest request) {
        try {
            logger.info("Creating payment for rental {} with amount {} and method {}", 
                rentalId, request.getAmount(), request.getPaymentMethod());
            
            String transactionId = "TX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            Payment payment = rentalPaymentService.createPayment(
                rentalId, 
                request.getAmount(), 
                request.getPaymentMethod(),
                request.getEmail(),
                transactionId
            );

            // 如果支付状态为 COMPLETED，发送邮件确认
            if ("COMPLETED".equals(payment.getStatus())) {
                String subject = "支付成功确认";
                String body = String.format("您的支付已成功！\n支付金额：%s\n交易ID：%s\n感谢您的使用！",
                        payment.getAmount(), payment.getTransactionId());
                sendEmail(request.getEmail(), subject, body, payment.getId());
                logger.info("支付成功邮件已发送至 {}", request.getEmail());
            }

            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            logger.error("Error creating payment for rental {}: {}", rentalId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        Payment payment = rentalPaymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<Payment>> getPaymentsByRental(@PathVariable Long rentalId) {
        List<Payment> payments = rentalPaymentService.getPaymentsByRentalId(rentalId);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Payment payment = rentalPaymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = rentalPaymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<Payment> payments = rentalPaymentService.getPaymentsByDateRange(start, end);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/simulate")
    public ResponseEntity<?> simulatePayment(@PathVariable Long id) {
        try {
            logger.info("Simulating payment for payment ID: {}", id);
            
            Payment payment = rentalPaymentService.getPaymentById(id);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }

            // 模拟支付成功
            payment = rentalPaymentService.updatePaymentStatus(id, "COMPLETED");
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            logger.error("Error simulating payment for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error simulating payment: " + e.getMessage());
        }
    }
} 