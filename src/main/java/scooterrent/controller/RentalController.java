package scooterrent.controller;

import scooterrent.dto.RentalDTO;
import scooterrent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<RentalDTO>> getUserRentals(@PathVariable String username) {
        return ResponseEntity.ok(rentalService.getUserRentals(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @PostMapping
    public ResponseEntity<RentalDTO> createRental(
            @RequestParam String username,
            @RequestParam Long scooterId,
            @RequestBody RentalDTO rentalDTO) {
        return ResponseEntity.ok(rentalService.createRental(username, scooterId, rentalDTO));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<RentalDTO> endRental(
            @PathVariable Long id,
            @RequestBody RentalDTO endDetails) {
        return ResponseEntity.ok(rentalService.endRental(id, endDetails));
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<RentalDTO> extendRental(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> extensionDetails) {
        try {
            Integer additionalHours = extensionDetails.get("extensionHours");
            if (additionalHours == null || additionalHours <= 0) {
                return ResponseEntity.badRequest().build();
            }
            RentalDTO updatedRental = rentalService.extendRental(id, additionalHours);
            return ResponseEntity.ok(updatedRental);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RentalDTO> cancelRental(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<Map<String, Double>> calculateRentalCost(@RequestBody Map<String, Object> params) {
        Long rentalId = Long.valueOf(params.get("rentalId").toString());
        String startTime = params.get("startTime").toString();
        String endTime = params.get("endTime").toString();
        String plan = params.get("plan").toString();
        
        double cost = rentalService.calculateRentalCost(rentalId, startTime, endTime, plan);
        return ResponseEntity.ok(Map.of("totalCost", cost));
    }
} 