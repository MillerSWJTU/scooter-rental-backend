package scooterrent.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_type")
    private String paymentType = "RENTAL"; // 只使用 RENTAL 类型

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status")
    private String status;

    @Column(name = "email", nullable = false) // 新增字段
    private String email;
} 