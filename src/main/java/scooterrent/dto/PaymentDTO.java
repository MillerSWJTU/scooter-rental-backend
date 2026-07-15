package scooterrent.dto;

import scooterrent.enums.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer id;
    private Long rentalId;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private String paymentType;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 用于模拟支付的字段
    private String redirectUrl;
    private String qrCodeUrl;
} 