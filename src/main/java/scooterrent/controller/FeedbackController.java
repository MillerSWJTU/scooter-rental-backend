package scooterrent.controller;

import scooterrent.dto.FeedbackDTO;
import scooterrent.dto.FeedbackRequestDTO;
import scooterrent.enums.FeedbackStatus;
import scooterrent.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 反馈接口
 * 权限：
 *   提交/查自己 — 登录即可 或 本人即管理员
 *   管理反馈（全部/状态/类型/回复/删除） — 管理员
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackRequestDTO requestDTO) {
        return ResponseEntity.ok(feedbackService.createFeedback(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Integer id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByUsername(@PathVariable String username) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByUsername(username));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByStatus(@PathVariable FeedbackStatus status) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByType(@PathVariable String type) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByType(type));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FeedbackDTO> updateFeedbackStatus(
            @PathVariable Integer id,
            @RequestParam FeedbackStatus status) {
        return ResponseEntity.ok(feedbackService.updateFeedbackStatus(id, status));
    }

    @PutMapping("/{id}/response")
    public ResponseEntity<FeedbackDTO> addAdminResponse(
            @PathVariable Integer id,
            @RequestParam String response) {
        return ResponseEntity.ok(feedbackService.addAdminResponse(id, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok().build();
    }
} 