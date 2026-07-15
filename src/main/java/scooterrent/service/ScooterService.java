package scooterrent.service;

import scooterrent.exception.BusinessException;
import scooterrent.dto.ScooterDTO;
import scooterrent.entity.Scooter;
import scooterrent.enums.ScooterStatus;
import scooterrent.repository.ScooterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import scooterrent.dto.ScooterStatsDTO;
import scooterrent.repository.RentalRepository;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class ScooterService {

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RentalRepository rentalRepository;

    @Transactional
    public ScooterDTO createScooter(ScooterDTO scooterDTO) {
        Scooter scooter = modelMapper.map(scooterDTO, Scooter.class);
        Scooter savedScooter = scooterRepository.save(scooter);
        return modelMapper.map(savedScooter, ScooterDTO.class);
    }

    public ScooterDTO getScooterById(Long id) {
        Optional<Scooter> scooter = scooterRepository.findById(id);
        return scooter.map(s -> modelMapper.map(s, ScooterDTO.class))
                .orElseThrow(() -> new BusinessException(404, "Scooter not found with id: " + id));
    }

    public List<ScooterDTO> getAllScooters() {
        List<Scooter> scooters = scooterRepository.findAll();
        Map<Long, Object[]> statsMap = new HashMap<>();
        for (Object[] row : rentalRepository.findScooterRentalStats()) {
            statsMap.put(((Number) row[0]).longValue(), row);
        }
        return scooters.stream().map(scooter -> {
            ScooterDTO dto = modelMapper.map(scooter, ScooterDTO.class);
            Object[] stat = statsMap.get(scooter.getId());
            dto.setRentalCount(stat == null ? 0 : ((Number) stat[1]).intValue());
            dto.setTotalRevenue(stat == null ? 0.0 : stat[2] == null ? 0.0 : ((Number) stat[2]).doubleValue());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ScooterDTO> getAvailableScooters() {
        List<Scooter> scooters = scooterRepository.findByStatus(ScooterStatus.AVAILABLE);
        return scooters.stream()
                .map(scooter -> modelMapper.map(scooter, ScooterDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteScooter(Long id) {
        if (scooterRepository.existsById(id)) {
            scooterRepository.deleteById(id);
        } else {
            throw new BusinessException(404, "Scooter not found with id: " + id);
        }
    }

    @Transactional
    public ScooterDTO updateScooterPrice(Long id, Double pricePerHour, Double pricePerFourHours, 
                                         Double pricePerDay, Double pricePerWeek) {
        Optional<Scooter> optionalScooter = scooterRepository.findById(id);
        if (optionalScooter.isPresent()) {
            Scooter scooter = optionalScooter.get();
            scooter.setPricePerHour(pricePerHour);
            scooter.setPricePerFourHours(pricePerFourHours);
            scooter.setPricePerDay(pricePerDay);
            scooter.setPricePerWeek(pricePerWeek);
            Scooter updatedScooter = scooterRepository.save(scooter);
            return modelMapper.map(updatedScooter, ScooterDTO.class);
        }
        throw new BusinessException(404, "Scooter not found with id: " + id);
    }

    @Transactional
    public ScooterDTO updateScooter(Long id, ScooterDTO scooterDTO) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Scooter not found with id: " + id));

        // Explicitly update only the relevant fields for general update
        // Exclude price fields - those are handled by updateScooterPrice
        if (scooterDTO.getModel() != null) {
            scooter.setModel(scooterDTO.getModel());
        }
        if (scooterDTO.getStatus() != null) {
            // Ensure status is a valid enum constant before setting
             try {
                ScooterStatus newStatus = scooterDTO.getStatus(); // Assumes DTO has correct Enum type
                scooter.setStatus(newStatus);
             } catch (IllegalArgumentException e) {
                 // Handle invalid status string if necessary, though validation should catch this
                 System.err.println("Invalid status value received in updateScooter: " + scooterDTO.getStatus());
                 // Optionally throw an exception or default to a safe status
             }
        }
        if (scooterDTO.getBatteryLevel() != null) {
             scooter.setBatteryLevel(scooterDTO.getBatteryLevel());
        }
        if (scooterDTO.getLocation() != null) {
            scooter.setLocation(scooterDTO.getLocation());
        }
         if (scooterDTO.getLatitude() != null) {
            scooter.setLatitude(scooterDTO.getLatitude());
        }
        if (scooterDTO.getLongitude() != null) {
            scooter.setLongitude(scooterDTO.getLongitude());
        }
        // Add any other non-price fields that should be updatable here
        // e.g., scooter.setImageUrl(scooterDTO.getImageUrl());

        Scooter updatedScooter = scooterRepository.save(scooter);
        
        // Return the updated DTO
        return modelMapper.map(updatedScooter, ScooterDTO.class);
    }

    public List<ScooterStatsDTO> getScooterStats() {
        List<Scooter> scooters = scooterRepository.findAll();
        Map<Long, Object[]> statsMap = new HashMap<>();
        for (Object[] row : rentalRepository.findScooterRentalStats()) {
            statsMap.put(((Number)row[0]).longValue(), row);
        }
        List<ScooterStatsDTO> result = new java.util.ArrayList<>();
        for (Scooter s : scooters) {
            Object[] stat = statsMap.get(s.getId());
            int rentalCount = stat == null ? 0 : ((Number)stat[1]).intValue();
            double totalRevenue = stat == null ? 0.0 : stat[2] == null ? 0.0 : ((Number)stat[2]).doubleValue();
            ScooterStatsDTO dto = new ScooterStatsDTO(
                s.getId(),
                s.getModel(),
                s.getBatteryLevel(),
                s.getStatus() != null ? s.getStatus().toString() : null,
                s.getLocation(),
                s.getPricePerHour(),
                rentalCount,
                totalRevenue
            );
            result.add(dto);
        }
        return result;
    }
}