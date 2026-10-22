package com.aquasafe.service;

import com.aquasafe.entity.Alert;
import com.aquasafe.repository.AlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public Alert createAlert(String district, String riskLevel, Double probability, String message) {
        Alert alert = new Alert(district, riskLevel, probability, message);
        return alertRepository.save(alert);
    }

    public List<Alert> getRecentAlerts() {
        return alertRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public Page<Alert> getAllAlerts(int page, int size) {
        return alertRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public List<Alert> getUnreadAlerts() {
        return alertRepository.findByStatusOrderByCreatedAtDesc("UNREAD");
    }

    public long getUnreadCount() {
        return alertRepository.countByStatus("UNREAD");
    }

    @Transactional
    public Alert markAsRead(Long id) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found with id: " + id));
        alert.setStatus("READ");
        return alertRepository.save(alert);
    }

    @Transactional
    public void markAllAsRead() {
        List<Alert> unread = alertRepository.findByStatusOrderByCreatedAtDesc("UNREAD");
        for (Alert a : unread) {
            a.setStatus("READ");
        }
        alertRepository.saveAll(unread);
    }
}
