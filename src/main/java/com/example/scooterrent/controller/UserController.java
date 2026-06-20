package com.example.scooterrent.controller;

import com.example.scooterrent.dto.UserDTO;
import com.example.scooterrent.dto.RegisterRequestDTO;
import com.example.scooterrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 允许所有源的跨域请求
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO registrationDTO) {
        return ResponseEntity.ok(userService.register(registrationDTO));
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String username,
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(username, userDTO));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-discount")
    @PreAuthorize("hasAnyRole('USER', 'DISCOUNT', 'ADMIN')")
    public ResponseEntity<?> verifyDiscount(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        userService.setUserRole(username, "ROLE_DISCOUNT");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/recharge")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> rechargeBalance(
            @PathVariable String username,
            @RequestBody java.util.Map<String, java.math.BigDecimal> request) {
        java.math.BigDecimal amount = request.get("amount");
        if (amount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.rechargeBalance(username, amount));
    }
} 