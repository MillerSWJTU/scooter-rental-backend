package scooterrent.controller;

import scooterrent.dto.RentalDTO;
import scooterrent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 租赁接口
 * 权限：
 *   GET /rentals/user/{username} — 本人或管理员
 *   其余 — 登录即可（USER / DISCOUNT / ADMIN）
 */
@RestController
@RequestMapping("/api/rentals")
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
    public ResponseEntity<RentalDTO> createRental(@RequestBody RentalDTO dto) {
        return ResponseEntity.ok(rentalService.createRental(dto.getUsername(), dto.getScooterId(), dto));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<RentalDTO> endRental(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(rentalService.endRental(id, body.get("endLocation")));
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<RentalDTO> extendRental(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer hours = body.get("additionalHours");
        if (hours == null || hours <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(rentalService.extendRental(id, hours));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RentalDTO> cancelRental(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(id));
    }
}
