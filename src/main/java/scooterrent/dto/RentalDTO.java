package scooterrent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalDTO {
    private Long id;
    private String username;
    private Long scooterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startLocation;
    private String endLocation;
    private Double totalCost;
    private Boolean active;
    private Integer extensionHours;
    private Integer duration; // 租赁时长（小时）
    private String status; // 添加状态字段
    private String plan; // 租赁方案：DAILY, WEEKLY, FOUR_HOURS, HOURLY
    private ScooterDTO scooter; // 添加完整的滑板车信息
} 