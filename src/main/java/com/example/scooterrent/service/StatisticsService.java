package com.example.scooterrent.service;

import com.example.scooterrent.repository.UserRepository;
import com.example.scooterrent.repository.RentalRepository;
import com.example.scooterrent.repository.RoleRepository;
import com.example.scooterrent.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    // 用户角色分布
    public List<Map<String, Object>> getUserRoleDistribution() {
        List<Role> roles = roleRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role role : roles) {
            long count = userRepository.countByRole(role);
            Map<String, Object> map = new HashMap<>();
            map.put("name", role.getName());
            map.put("value", count);
            result.add(map);
        }
        return result;
    }

    // 一周内每天的总收入（用rentals表）
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

    // 总用户数、订单总数、总收入（用rentals表）
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", userRepository.count());
        map.put("totalOrders", rentalRepository.count());
        Double totalRevenue = rentalRepository.sumTotalCost();
        map.put("totalRevenue", totalRevenue == null ? 0 : totalRevenue);
        return map;
    }
}
