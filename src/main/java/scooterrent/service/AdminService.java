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
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByScooterId(Long scooterId) {
        return rentalRepository.findByScooterId(scooterId);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalOrders() {
        return rentalRepository.count();
    }

    public double getTotalRevenue() {
        Double sum = rentalRepository.sumTotalCost();
        return sum != null ? sum : 0.0;
    }

    public List<Map<String, Object>> getUserRoleDistribution() {
        List<Role> roles = roleRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role role : roles) {
            long count = userRepository.countByRole(role);
            Map<String, Object> roleData = new HashMap<>();
            roleData.put("name", role.getName());
            roleData.put("value", count);
            result.add(roleData);
        }
        return result;
    }

    public List<Map<String, Object>> getWeeklyRentalIncome() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        // 用日期范围查询，只拿7天的数据
        List<Rental> rentals = rentalRepository.findByStartTimeBetween(
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("HOURLY", 0.0);
            dayData.put("FOUR_HOURS", 0.0);
            dayData.put("DAILY", 0.0);
            dayData.put("WEEKLY", 0.0);
            dayData.put("TOTAL", 0.0);
            result.add(dayData);
        }

        for (Rental rental : rentals) {
            if (rental.getStartTime() == null || rental.getTotalCost() == null || rental.getPlan() == null) {
                continue;
            }
            String dateStr = rental.getStartTime().toLocalDate().format(formatter);
            for (Map<String, Object> day : result) {
                if (day.get("date").equals(dateStr)) {
                    day.put(rental.getPlan(), (Double) day.get(rental.getPlan()) + rental.getTotalCost());
                    day.put("TOTAL", (Double) day.get("TOTAL") + rental.getTotalCost());
                    break;
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> getRentalPlanStats() {
        // 用 GROUP BY 查询，数据库直接聚合
        List<Object[]> rows = rentalRepository.countAndSumByPlan();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            String dbPlan = row[0] != null ? row[0].toString() : "UNKNOWN";
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0;
            double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            String planName;
            switch (dbPlan) {
                case "HOURLY":    planName = "1 Hour"; break;
                case "FOUR_HOURS": planName = "4 Hours"; break;
                case "DAILY":     planName = "1 Day"; break;
                case "WEEKLY":    planName = "1 Week"; break;
                default:          continue;
            }

            Map<String, Object> planData = new HashMap<>();
            planData.put("plan", planName);
            planData.put("count", count);
            planData.put("value", count);
            planData.put("revenue", revenue);
            result.add(planData);
        }
        return result;
    }

    public List<Map<String, Object>> getCombinedDailyIncome() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        // 用日期范围查询
        List<Rental> rentals = rentalRepository.findByStartTimeBetween(
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("income", 0.0);
            result.add(dayData);
        }

        for (Rental rental : rentals) {
            if (rental.getStartTime() == null || rental.getTotalCost() == null) {
                continue;
            }
            String dateStr = rental.getStartTime().toLocalDate().format(formatter);
            for (Map<String, Object> day : result) {
                if (day.get("date").equals(dateStr)) {
                    day.put("income", (Double) day.get("income") + rental.getTotalCost());
                    break;
                }
            }
        }
        return result;
    }

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

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", getTotalUsers());
        stats.put("totalOrders", getTotalOrders());
        stats.put("totalRevenue", getTotalRevenue());
        return stats;
    }
}
