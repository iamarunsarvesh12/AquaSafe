package com.aquasafe.controller;

import com.aquasafe.entity.Alert;
import com.aquasafe.service.AlertService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getRecentAlerts() {
        return ResponseEntity.ok(alertService.getRecentAlerts());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Alert>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(alertService.getAllAlerts(page, size));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Alert>> getUnreadAlerts() {
        return ResponseEntity.ok(alertService.getUnreadAlerts());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(alertService.getUnreadCount());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Alert> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.markAsRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<java.util.Map<String, Object>> markAllAsRead() {
        alertService.markAllAsRead();
        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("message", "All alerts marked as read");
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}
