package scooterrent.dto;

import scooterrent.enums.RentalStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalDTO {
    private Long id;
    private String username;
    private Long scooterId;
    private String scooterModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startLocation;
    private String endLocation;
    private Double totalCost;
    private Integer duration;
    private String plan;
    private RentalStatus status;
}
