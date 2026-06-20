package scooterrent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderRequestDTO {
    private Long scooterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 