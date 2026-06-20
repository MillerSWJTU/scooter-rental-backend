package scooterrent.controller;

import scooterrent.dto.ScooterDTO;
import scooterrent.dto.ScooterPriceUpdateRequest;
import scooterrent.dto.ScooterStatsDTO;
import scooterrent.enums.ScooterStatus;
import scooterrent.service.ScooterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/scooters")
@CrossOrigin(origins = "*")
@Tag(name = "Scooter Management", description = "APIs for managing scooters")
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Scooter", description = "Create a new scooter")
    public ResponseEntity<ScooterDTO> createScooter(@RequestBody ScooterDTO scooterDTO) {
        return ResponseEntity.ok(scooterService.createScooter(scooterDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Scooter Details", description = "Get scooter details by ID")
    public ResponseEntity<ScooterDTO> getScooterById(
            @Parameter(description = "Scooter ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(scooterService.getScooterById(id));
    }

    @GetMapping
    @Operation(summary = "Get All Scooters", description = "Get list of all scooters")
    public ResponseEntity<List<ScooterDTO>> getAllScooters() {
        return ResponseEntity.ok(scooterService.getAllScooters());
    }

    @GetMapping("/available")
    @Operation(summary = "Get Available Scooters", description = "Get list of all available scooters")
    public ResponseEntity<List<ScooterDTO>> getAvailableScooters() {
        return ResponseEntity.ok(scooterService.getAvailableScooters());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Scooter", description = "Delete a specific scooter")
    public ResponseEntity<Void> deleteScooter(
            @Parameter(description = "Scooter ID", required = true)
            @PathVariable Long id) {
        scooterService.deleteScooter(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Scooter Pricing", description = "Update the pricing information of a scooter")
    public ResponseEntity<ScooterDTO> updateScooterPrice(
            @Parameter(description = "Scooter ID", required = true)
            @PathVariable Long id,
            @RequestBody ScooterPriceUpdateRequest priceRequest) {
        return ResponseEntity.ok(scooterService.updateScooterPrice(id, 
                                                                priceRequest.getPricePerHour(),
                                                                priceRequest.getPricePerFourHours(),
                                                                priceRequest.getPricePerDay(),
                                                                priceRequest.getPricePerWeek()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Scooter", description = "Update details of a specific scooter")
    public ResponseEntity<ScooterDTO> updateScooter(
            @Parameter(description = "Scooter ID", required = true)
            @PathVariable Long id,
            @RequestBody ScooterDTO scooterDTO) {
        return ResponseEntity.ok(scooterService.updateScooter(id, scooterDTO));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ScooterStatsDTO>> getScooterStats() {
        return ResponseEntity.ok(scooterService.getScooterStats());
    }
} 