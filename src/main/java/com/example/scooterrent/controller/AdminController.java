package com.example.scooterrent.controller;

import com.example.scooterrent.entity.Rental;
import com.example.scooterrent.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private AdminService adminService;

    @GetMapping("/rental-income/weekly")
    public ResponseEntity<?> getWeeklyRentalIncome() {
        try {
            List<Map<String, Object>> result = adminService.getWeeklyRentalIncome();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching weekly rental income: ", e);
            return ResponseEntity.status(500).body("Error fetching weekly rental income: " + e.getMessage());
        }
    }

    @GetMapping("/rental-income/scooters/{scooterId}")
    public ResponseEntity<?> getScooterWeeklyIncome(@PathVariable("scooterId") Long scooterId) {
        try {
            boolean isSpecialScooter = (scooterId == 19L || scooterId == 20L);
            if (!isSpecialScooter) {
                return ResponseEntity.ok(
                    Map.of("message", "Detailed weekly income chart is only available for scooters with ID 19 or 20")
                );
            }
            
            List<Rental> rentals = adminService.getRentalsByScooterId(scooterId);
            Map<String, Double> weeklyIncome = new TreeMap<>();

            LocalDateTime now = LocalDateTime.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = now.get(weekFields.weekOfWeekBasedYear());

            rentals.stream()
                .filter(rental -> rental.getEndTime() != null && rental.getTotalCost() != null)
                .forEach(rental -> {
                    LocalDateTime rentalDate = rental.getEndTime();
                    if (rentalDate != null) {
                        int rentalWeek = rentalDate.get(weekFields.weekOfWeekBasedYear());
                        
                        if (rentalWeek == currentWeek) {
                            String date = rentalDate.toLocalDate().toString();
                            weeklyIncome.merge(date, rental.getTotalCost(), Double::sum);
                        }
                    }
                });
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Map.Entry<String, Double> entry : weeklyIncome.entrySet()) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", entry.getKey());
                dayData.put("income", entry.getValue());
                result.add(dayData);
            }
            
            if (result.isEmpty()) {
                for (int i = 0; i < 7; i++) {
                    Map<String, Object> mockData = new HashMap<>();
                    mockData.put("date", now.minusDays(6 - i).toLocalDate().toString());
                    mockData.put("income", 0.0);
                    result.add(mockData);
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching scooter weekly income: ", e);
            return ResponseEntity.status(500).body("Error fetching scooter weekly income: " + e.getMessage());
        }
    }

    @GetMapping("/rental-income/combined-daily")
    public ResponseEntity<?> getCombinedDailyIncome() {
        try {
            List<Map<String, Object>> result = adminService.getCombinedDailyIncome();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching combined daily income: ", e);
            return ResponseEntity.status(500).body("Error fetching combined daily income: " + e.getMessage());
        }
    }

    @GetMapping("/rental-stats/plans")
    public ResponseEntity<?> getRentalPlanStats() {
        try {
            List<Map<String, Object>> result = adminService.getRentalPlanStats();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching rental plan stats: ", e);
            return ResponseEntity.status(500).body("Error fetching rental plan stats: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> result = adminService.getDashboardStats();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats: ", e);
            return ResponseEntity.status(500).body("Error fetching dashboard stats: " + e.getMessage());
        }
    }

    @GetMapping("/users/role-distribution")
    public ResponseEntity<?> getUserRoleDistribution() {
        try {
            List<Map<String, Object>> result = adminService.getUserRoleDistribution();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching user role distribution: ", e);
            return ResponseEntity.status(500).body("Error fetching user role distribution: " + e.getMessage());
        }
    }
}
