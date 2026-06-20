package scooterrent.service;

import scooterrent.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private AdminService adminService;

    // 用户角色分布 → 委托给 AdminService（单一来源）
    public List<Map<String, Object>> getUserRoleDistribution() {
        return adminService.getUserRoleDistribution();
    }

    // 一周内每天的总收入（用rentals表，按createdAt统计）
    public List<Map<String, Object>> getDailyRevenue() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = day.plusDays(1).atStartOfDay();
            Double sum = rentalRepository.sumTotalCostByCreatedAtBetween(start, end);
            Map<String, Object> map = new HashMap<>();
            map.put("name", day.getDayOfWeek().toString());
            map.put("revenue", sum == null ? 0 : sum);
            result.add(map);
        }
        return result;
    }

    // 仪表盘总览 → 委托给 AdminService（单一来源）
    public Map<String, Object> getDashboardStats() {
        return adminService.getDashboardStats();
    }
}
