package scooterrent.service;

import scooterrent.entity.Payment;
import scooterrent.entity.Rental;
import scooterrent.repository.PaymentRepository;
import scooterrent.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RentalPaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private scooterrent.repository.UserRepository userRepository;

    @Transactional
    public Payment createPayment(Long rentalId, BigDecimal amount, String paymentMethod, String email, String transactionId) {
        try {
            logger.info("Creating payment for rental {} with amount {} and method {} and email {}", 
                rentalId, amount, paymentMethod, email);

            Rental rental = rentalRepository.findById(rentalId)
                    .orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Payment amount must be greater than zero");
            }

            // 如果是钱包支付，扣除余额
            if ("WALLET".equalsIgnoreCase(paymentMethod)) {
                scooterrent.entity.User user = rental.getUser();
                if (user.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient wallet balance");
                }
                user.setBalance(user.getBalance().subtract(amount));
                userRepository.save(user);
            }

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setAmount(amount);
            payment.setPaymentMethod(paymentMethod);
            payment.setTransactionId(transactionId);
            payment.setPaymentTime(LocalDateTime.now());
            payment.setEmail(email);
            payment.setStatus("COMPLETED");

            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment created successfully with id: {}", savedPayment.getId());
            return savedPayment;
        } catch (Exception e) {
            logger.error("Error creating payment for rental {}: {}", rentalId, e.getMessage(), e);
            throw new RuntimeException("Error creating payment: " + e.getMessage());
        }
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getPaymentsByRentalId(Long rentalId) {
        return paymentRepository.findByRentalId(rentalId);
    }

    @Transactional
    public Payment updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentTimeBetween(start, end);
    }
} 