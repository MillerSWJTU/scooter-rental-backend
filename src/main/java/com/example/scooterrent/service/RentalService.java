package com.example.scooterrent.service;

import com.example.scooterrent.dto.RentalDTO;
import com.example.scooterrent.dto.ScooterDTO;
import com.example.scooterrent.entity.Rental;
import com.example.scooterrent.entity.Scooter;
import com.example.scooterrent.enums.ScooterStatus;
import com.example.scooterrent.entity.User;
import com.example.scooterrent.repository.RentalRepository;
import com.example.scooterrent.repository.ScooterRepository;
import com.example.scooterrent.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.scooterrent.dto.RentalPlanStatsDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<RentalDTO> getUserRentals(String username) {
        return rentalRepository.findByUserUsername(username)
                .stream()
                .map(rental -> {
                    RentalDTO dto = new RentalDTO();
                    // 手动映射基本字段
                    dto.setId(rental.getId());
                    dto.setUsername(rental.getUser().getUsername());
                    dto.setScooterId(rental.getScooter().getId());
                    dto.setStartTime(rental.getStartTime());
                    dto.setEndTime(rental.getEndTime());
                    dto.setStartLocation(rental.getStartLocation());
                    dto.setEndLocation(rental.getEndLocation());
                    dto.setTotalCost(rental.getTotalCost());
                    dto.setActive(rental.isActive());
                    dto.setDuration(rental.getDuration());
                    dto.setPlan(rental.getPlan());
                    dto.setStatus(rental.getStatus());
                    
                    // 映射滑板车信息
                    if (rental.getScooter() != null) {
                        ScooterDTO scooterDTO = new ScooterDTO();
                        scooterDTO.setId(rental.getScooter().getId());
                        scooterDTO.setModel(rental.getScooter().getModel());
                        scooterDTO.setStatus(rental.getScooter().getStatus());
                        scooterDTO.setBatteryLevel(rental.getScooter().getBatteryLevel());
                        scooterDTO.setLocation(rental.getScooter().getLocation());
                        scooterDTO.setLatitude(rental.getScooter().getLatitude());
                        scooterDTO.setLongitude(rental.getScooter().getLongitude());
                        dto.setScooter(scooterDTO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public RentalDTO getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        RentalDTO dto = modelMapper.map(rental, RentalDTO.class);
        dto.setStatus(rental.getStatus());
        return dto;
    }

    @Transactional
    public RentalDTO createRental(String username, Long scooterId, RentalDTO rentalDetails) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new RuntimeException("Scooter not found"));

        if (scooter.getStatus() != ScooterStatus.AVAILABLE) {
            throw new RuntimeException("Scooter is not available");
        }

        if (rentalDetails.getDuration() == null || rentalDetails.getDuration() <= 0) {
            throw new RuntimeException("Invalid rental duration");
        }

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setScooter(scooter);
        rental.setStartTime(LocalDateTime.now());
        rental.setStartLocation(rentalDetails.getStartLocation());
        rental.setActive(true);
        rental.setDuration(rentalDetails.getDuration());
        rental.setPlan(rentalDetails.getPlan());

        rental.setEndTime(rental.getStartTime().plusHours(rental.getDuration()));

        double totalCost = calculateTotalCost(scooter, rental.getStartTime(), rental.getEndTime(), rental.getPlan(), rental.getDuration());
        rental.setTotalCost(totalCost);

        scooter.setStatus(ScooterStatus.RENTED);
        scooterRepository.save(scooter);

        Rental savedRental = rentalRepository.save(rental);
        return modelMapper.map(savedRental, RentalDTO.class);
    }

    @Transactional
    public RentalDTO endRental(Long id, RentalDTO endDetails) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.isActive()) {
            throw new RuntimeException("Rental is not active");
        }

        LocalDateTime endTime = LocalDateTime.now();
        rental.setEndTime(endTime);
        rental.setEndLocation(endDetails.getEndLocation());
        rental.setActive(false);

        // Calculate and set total cost
        double totalCost = calculateTotalCost(rental.getScooter(), rental.getStartTime(), endTime, rental.getPlan(), rental.getDuration());
        rental.setTotalCost(totalCost);

        Scooter scooter = rental.getScooter();
        scooter.setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        Rental savedRental = rentalRepository.save(rental);
        RentalDTO dto = modelMapper.map(savedRental, RentalDTO.class);
        dto.setStatus("COMPLETED");
        return dto;
    }

    @Transactional
    public RentalDTO extendRental(Long id, Integer additionalHours) {
        if (id == null) {
            throw new RuntimeException("Rental ID cannot be null");
        }
        
        if (additionalHours == null || additionalHours <= 0) {
            throw new RuntimeException("Additional hours must be greater than zero");
        }
        
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.isActive()) {
            throw new RuntimeException("Rental is not active");
        }

        try {
            // 更新租赁时长
            rental.setDuration(rental.getDuration() + additionalHours);
            
            // 更新结束时间
            rental.setEndTime(rental.getStartTime().plusHours(rental.getDuration()));
            
            // 重新计算费用
            double cost = calculateTotalCost(
                rental.getScooter(), 
                rental.getStartTime(), 
                rental.getEndTime(), 
                rental.getPlan(), 
                rental.getDuration()
            );
            rental.setTotalCost(cost);

            // 保存更新后的租赁信息
            Rental updatedRental = rentalRepository.save(rental);
            
            // 返回更新后的租赁信息
            RentalDTO dto = modelMapper.map(updatedRental, RentalDTO.class);
            dto.setStatus(updatedRental.getStatus());
            dto.setExtensionHours(additionalHours); // 设置扩展时间
        return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error extending rental: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RentalDTO cancelRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.isActive()) {
            throw new RuntimeException("Rental is not active");
        }

        rental.setActive(false);
        rental.setEndTime(LocalDateTime.now());
        rental.setTotalCost(0.0); // 确保取消时费用为0
        rental.setStatus("CANCELLED"); // 显式设置状态

        Scooter scooter = rental.getScooter();
        scooter.setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        Rental savedRental = rentalRepository.save(rental);
        RentalDTO dto = modelMapper.map(savedRental, RentalDTO.class);
        dto.setStatus("CANCELLED"); // 确保状态正确设置
        return dto;
    }

    public double calculateRentalCost(Long rentalId, String startTime, String endTime, String plan) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        
        Scooter scooter = rental.getScooter();
        if (scooter == null) {
            throw new RuntimeException("Scooter not found for rental");
        }

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        return calculateTotalCost(scooter, start, end, plan, rental.getDuration());
    }

    private double calculateTotalCost(Scooter scooter, LocalDateTime startTime, LocalDateTime endTime, String plan, Integer duration) {
        if (plan == null) {
            plan = "HOURLY"; // Default to hourly if plan is not specified
        }
        double price = 0;
        switch (plan) {
            case "DAILY":
                price = duration * scooter.getPricePerDay();
                break;
            case "WEEKLY":
                price = duration * scooter.getPricePerWeek();
                break;
            case "FOUR_HOURS":
                price = duration * scooter.getPricePerFourHours();
                break;
            case "HOURLY":
            default:
        long hours = java.time.Duration.between(startTime, endTime).toHours();
        if (hours == 0) {
            hours = 1; // Minimum rental period is 1 hour
        }
                price = hours * scooter.getPricePerHour();
        }
        // 8折优惠逻辑
        // 如果有用户信息且角色为ROLE_DISCOUNT，则打8折
        // 这里假设调用方有user字段（如createRental等），否则需传入user参数
        try {
            // 尝试从当前线程上下文获取用户
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof com.example.scooterrent.entity.User) {
                com.example.scooterrent.entity.User user = (com.example.scooterrent.entity.User) auth.getPrincipal();
                if (user.getRole() != null && "ROLE_DISCOUNT".equals(user.getRole().getName())) {
                    price = price * 0.8;
                }
            }
        } catch (Exception e) {
            // 忽略异常，保持原价
        }
        return price;
    }

    public List<RentalPlanStatsDTO> getRentalPlanStats() {
        List<Object[]> results = rentalRepository.countAndSumByPlan();
        List<RentalPlanStatsDTO> stats = new ArrayList<>();
        for (Object[] row : results) {
            String plan = row[0] == null ? "UNKNOWN" : row[0].toString();
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            double revenue = row[2] == null ? 0.0 : ((Number) row[2]).doubleValue();
            stats.add(new RentalPlanStatsDTO(plan, count, revenue));
        }
        return stats;
    }
} 