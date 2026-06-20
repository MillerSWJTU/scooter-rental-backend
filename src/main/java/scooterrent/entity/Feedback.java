package scooterrent.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import scooterrent.enums.FeedbackStatus;
import scooterrent.enums.FeedbackType;

@Entity
@Table(name = "feedbacks")
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    private FeedbackType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    private FeedbackStatus status;

    @Column(name = "admin_response")
    private String adminResponse;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = FeedbackStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == FeedbackStatus.RESOLVED && resolvedAt == null) {
            resolvedAt = LocalDateTime.now();
        }
    }
} 