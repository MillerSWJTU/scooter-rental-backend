package com.example.scooterrent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    // ID字段保留但作为可选字段，用于兼容现有前端代码
    private Integer id;
    
    // 用户名现在作为主键
    private String username;
    private String email;
    private String phone;
    private String role;
    private java.math.BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 