package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history")
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "action_taken", nullable = false, length = 100)
    private String actionTaken;

    @Column(name = "acknowledged_by", nullable = false, length = 50)
    private String acknowledgedBy;

    @Column(name = "action_timestamp")
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String comments;

    public AlertHistory() {}

    public AlertHistory(Long alertId, String district, String actionTaken, String acknowledgedBy, String comments) {
        this.alertId = alertId;
        this.district = district;
        this.actionTaken = actionTaken;
        this.acknowledgedBy = acknowledgedBy;
        this.comments = comments;
        this.actionTimestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public LocalDateTime getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

