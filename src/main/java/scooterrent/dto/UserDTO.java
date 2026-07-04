package scooterrent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;

    private String username;
    private String email;
    private String phone;
    private String role;
    private java.math.BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 