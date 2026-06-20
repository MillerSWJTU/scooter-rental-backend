package scooterrent.controller;

import scooterrent.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // 用户角色分布
    @GetMapping("/user-role-distribution")
    public ResponseEntity<List<Map<String, Object>>> getUserRoleDistribution() {
        return ResponseEntity.ok(statisticsService.getUserRoleDistribution());
    }

    // 每日收入
    @GetMapping("/daily-revenue")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue() {
        return ResponseEntity.ok(statisticsService.getDailyRevenue());
    }

    // 仪表盘总览
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(statisticsService.getDashboardStats());
    }
}
