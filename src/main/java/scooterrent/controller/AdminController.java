package scooterrent.controller;

import scooterrent.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台接口（仪表盘/统计）
 * 权限：管理员专属
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/revenue/weekly")
    public ResponseEntity<List<Map<String, Object>>> getRevenueWeekly() {
        return ResponseEntity.ok(adminService.getRevenueWeekly());
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<List<Map<String, Object>>> getRevenueDaily() {
        return ResponseEntity.ok(adminService.getRevenueDaily());
    }

    @GetMapping("/revenue/scooters/{scooterId}")
    public ResponseEntity<List<Map<String, Object>>> getScooterRevenue(@PathVariable Long scooterId) {
        return ResponseEntity.ok(adminService.getScooterRevenue(scooterId));
    }

    @GetMapping("/rental-plans")
    public ResponseEntity<List<Map<String, Object>>> getRentalPlanStats() {
        return ResponseEntity.ok(adminService.getRentalPlanStats());
    }

    @GetMapping("/user-role-distribution")
    public ResponseEntity<List<Map<String, Object>>> getUserRoleDistribution() {
        return ResponseEntity.ok(adminService.getUserRoleDistribution());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }
}
