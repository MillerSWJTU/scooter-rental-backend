package scooterrent.controller;

import scooterrent.dto.RegisterRequestDTO;
import scooterrent.dto.UserDTO;
import scooterrent.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户接口
 * 权限：
 *   GET/POST /users           — 管理员
 *   DELETE /users/{username}  — 管理员
 *   PUT /users/{username}/role — 管理员
 *   GET/PUT /users/{username} — 本人或管理员
 *   PUT password / POST recharge — 本人或管理员
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String username,
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(username, userDTO));
    }

    @PutMapping("/{username}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {
        userService.setUserRole(username, body.get("role"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {
        userService.updatePassword(username, body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/recharge")
    public ResponseEntity<UserDTO> rechargeBalance(
            @PathVariable String username,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        if (amount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.rechargeBalance(username, amount));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
