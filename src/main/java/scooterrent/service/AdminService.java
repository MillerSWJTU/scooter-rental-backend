package scooterrent.service;

import scooterrent.entity.Rental;
import scooterrent.entity.Role;
import scooterrent.repository.RentalRepository;
import scooterrent.repository.RoleRepository;
import scooterrent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class AdminService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd");

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // ==================== 收入 ====================

    /** 近7天收入，按方案分组 */
    public List<Map<String, Object>> getRevenueWeekly() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<Rental> rentals = rentalRepository.findByStartTimeBetween(
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", startDate.plusDays(i).format(DATE_FMT));
            day.put("HOURLY", 0.0);
            day.put("FOUR_HOURS", 0.0);
            day.put("DAILY", 0.0);
            day.put("WEEKLY", 0.0);
            day.put("TOTAL", 0.0);
            result.add(day);
        }

        for (Rental r : rentals) {
            if (r.getStartTime() == null || r.getTotalCost() == null || r.getPlan() == null) continue;
            String ds = r.getStartTime().toLocalDate().format(DATE_FMT);
            result.stream().filter(d -> d.get("date").equals(ds)).findFirst().ifPresent(d -> {
                d.put(r.getPlan(), (Double) d.get(r.getPlan()) + r.getTotalCost());
                d.put("TOTAL", (Double) d.get("TOTAL") + r.getTotalCost());
            });
        }
        return result;
    }

    /** 近7天收入，按天汇总 */
    public List<Map<String, Object>> getRevenueDaily() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<Rental> rentals = rentalRepository.findByStartTimeBetween(
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", startDate.plusDays(i).format(DATE_FMT));
            day.put("income", 0.0);
            result.add(day);
        }

        for (Rental r : rentals) {
            if (r.getStartTime() == null || r.getTotalCost() == null) continue;
            String ds = r.getStartTime().toLocalDate().format(DATE_FMT);
            result.stream().filter(d -> d.get("date").equals(ds)).findFirst()
                    .ifPresent(d -> d.put("income", (Double) d.get("income") + r.getTotalCost()));
        }
        return result;
    }

    /** 某台滑板车本周每日收入 */
    public List<Map<String, Object>> getScooterRevenue(Long scooterId) {
        List<Rental> rentals = rentalRepository.findByScooterId(scooterId);
        Map<String, Double> dailyIncome = new TreeMap<>();
        LocalDateTime now = LocalDateTime.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int currentWeek = now.get(wf.weekOfWeekBasedYear());

        rentals.stream()
                .filter(r -> r.getEndTime() != null && r.getTotalCost() != null)
                .filter(r -> r.getEndTime().get(wf.weekOfWeekBasedYear()) == currentWeek)
                .forEach(r -> dailyIncome.merge(r.getEndTime().toLocalDate().toString(), r.getTotalCost(), Double::sum));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Double> e : dailyIncome.entrySet()) {
            result.add(Map.of("date", e.getKey(), "income", e.getValue()));
        }
        if (result.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                result.add(Map.of("date", now.minusDays(6 - i).toLocalDate().toString(), "income", 0.0));
            }
        }
        return result;
    }

    // ==================== 统计 ====================

    /** 租赁方案统计 */
    public List<Map<String, Object>> getRentalPlanStats() {
        List<Object[]> rows = rentalRepository.countAndSumByPlan();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> planNames = Map.of(
                "HOURLY", "1 Hour", "FOUR_HOURS", "4 Hours", "DAILY", "1 Day", "WEEKLY", "1 Week");
        for (Object[] row : rows) {
            String plan = row[0] != null ? row[0].toString() : null;
            if (plan == null || !planNames.containsKey(plan)) continue;
            result.add(Map.of(
                    "plan", planNames.get(plan),
                    "count", ((Number) row[1]).longValue(),
                    "revenue", row[2] != null ? ((Number) row[2]).doubleValue() : 0.0));
        }
        return result;
    }

    /** 用户角色分布 */
    public List<Map<String, Object>> getUserRoleDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role role : roleRepository.findAll()) {
            result.add(Map.of("name", role.getName(), "value", userRepository.countByRole(role)));
        }
        return result;
    }

    /** 仪表盘总览 */
    public Map<String, Object> getDashboard() {
        Double revenue = rentalRepository.sumTotalCost();
        return Map.of(
                "totalUsers", userRepository.count(),
                "totalRentals", rentalRepository.count(),
                "totalRevenue", revenue != null ? revenue : 0.0);
    }
}
