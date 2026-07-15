package scooterrent.service;

import scooterrent.dto.RentalDTO;
import scooterrent.entity.Rental;
import scooterrent.entity.Scooter;
import scooterrent.entity.User;
import scooterrent.enums.RentalStatus;
import scooterrent.enums.ScooterStatus;
import scooterrent.exception.BusinessException;
import scooterrent.repository.RentalRepository;
import scooterrent.repository.ScooterRepository;
import scooterrent.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        return rentalRepository.findByUserUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RentalDTO getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Rental not found"));
        return toDTO(rental);
    }

    @Transactional
    public RentalDTO createRental(String username, Long scooterId, RentalDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "User not found"));
        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new BusinessException(404, "Scooter not found"));

        if (scooter.getStatus() != ScooterStatus.AVAILABLE) {
            throw new BusinessException(400, "Scooter is not available");
        }
        if (dto.getDuration() == null || dto.getDuration() <= 0) {
            throw new BusinessException(400, "Invalid rental duration");
        }

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setScooter(scooter);
        rental.setStartTime(LocalDateTime.now());
        rental.setStartLocation(dto.getStartLocation());
        rental.setDuration(dto.getDuration());
        rental.setPlan(dto.getPlan() != null ? dto.getPlan() : "HOURLY");
        rental.setEndTime(rental.getStartTime().plusHours(dto.getDuration()));
        rental.setTotalCost(calculateCost(scooter, rental));

        scooter.setStatus(ScooterStatus.RENTED);
        scooterRepository.save(scooter);

        return toDTO(rentalRepository.save(rental));
    }

    @Transactional
    public RentalDTO endRental(Long id, String endLocation) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Rental not found"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BusinessException(400, "Rental is not active");
        }

        rental.setEndTime(LocalDateTime.now());
        rental.setEndLocation(endLocation);
        rental.setStatus(RentalStatus.COMPLETED);
        rental.setTotalCost(calculateCost(rental.getScooter(), rental));

        rental.getScooter().setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(rental.getScooter());

        return toDTO(rentalRepository.save(rental));
    }

    @Transactional
    public RentalDTO extendRental(Long id, Integer additionalHours) {
        if (id == null) {
            throw new BusinessException(400, "Rental ID cannot be null");
        }
        if (additionalHours == null || additionalHours <= 0) {
            throw new BusinessException(400, "Additional hours must be greater than zero");
        }

        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Rental not found"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BusinessException(400, "Rental is not active");
        }

        rental.setDuration(rental.getDuration() + additionalHours);
        rental.setEndTime(rental.getStartTime().plusHours(rental.getDuration()));
        rental.setTotalCost(calculateCost(rental.getScooter(), rental));

        return toDTO(rentalRepository.save(rental));
    }

    @Transactional
    public RentalDTO cancelRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Rental not found"));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BusinessException(400, "Rental is not active");
        }

        rental.setStatus(RentalStatus.CANCELLED);
        rental.setEndTime(LocalDateTime.now());
        rental.setTotalCost(0.0);

        rental.getScooter().setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(rental.getScooter());

        return toDTO(rentalRepository.save(rental));
    }

    // ==================== 计费 ====================

    private double calculateCost(Scooter scooter, Rental rental) {
        String plan = rental.getPlan() != null ? rental.getPlan() : "HOURLY";
        int duration = rental.getDuration();
        double price;

        switch (plan) {
            case "WEEKLY":
                price = Math.ceil(duration / (24.0 * 7)) * scooter.getPricePerWeek();
                break;
            case "DAILY":
                price = Math.ceil(duration / 24.0) * scooter.getPricePerDay();
                break;
            case "FOUR_HOURS":
                price = Math.ceil(duration / 4.0) * scooter.getPricePerFourHours();
                break;
            case "HOURLY":
            default:
                long hours = Duration.between(rental.getStartTime(), rental.getEndTime()).toHours();
                price = Math.max(1, hours) * scooter.getPricePerHour();
        }

        if (hasDiscount()) {
            price *= 0.8;
        }
        return price;
    }

    private boolean hasDiscount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_DISCOUNT"::equals);
    }

    // ==================== DTO 映射 ====================

    private RentalDTO toDTO(Rental rental) {
        RentalDTO dto = modelMapper.map(rental, RentalDTO.class);
        if (rental.getUser() != null) {
            dto.setUsername(rental.getUser().getUsername());
        }
        if (rental.getScooter() != null) {
            dto.setScooterId(rental.getScooter().getId());
            dto.setScooterModel(rental.getScooter().getModel());
        }
        return dto;
    }
}
