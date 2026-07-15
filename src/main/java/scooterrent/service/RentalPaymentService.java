package scooterrent.service;

import scooterrent.entity.Payment;
import scooterrent.entity.Rental;
import scooterrent.enums.PaymentStatus;
import scooterrent.exception.BusinessException;
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
        logger.info("Creating payment for rental {} with amount {} and method {}",
                rentalId, amount, paymentMethod);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new BusinessException(404, "Rental not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "Payment amount must be greater than zero");
        }

        if ("WALLET".equalsIgnoreCase(paymentMethod)) {
            scooterrent.entity.User user = rental.getUser();
            if (user.getBalance().compareTo(amount) < 0) {
                throw new BusinessException(400, "Insufficient wallet balance");
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
        payment.setStatus(PaymentStatus.COMPLETED);

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Payment not found"));
    }

    public List<Payment> getPaymentsByRentalId(Long rentalId) {
        return paymentRepository.findByRentalId(rentalId);
    }

    @Transactional
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentTimeBetween(start, end);
    }
}
