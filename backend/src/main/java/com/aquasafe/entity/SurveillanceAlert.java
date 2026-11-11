package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "surveillance_alerts", indexes = {
    @Index(name = "idx_alerts_dist", columnList = "district"),
    @Index(name = "idx_alerts_stat", columnList = "status"),
    @Index(name = "idx_alerts_time", columnList = "createdAt")
})
public class SurveillanceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(length = 100)
    private String disease = "General Water-Borne Risk";

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    private Double probability;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "triggering_factors", columnDefinition = "TEXT")
    private String triggeringFactors;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(nullable = false, length = 20)
    private String status = "UNREAD"; // UNREAD, READ, ESCALATED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SurveillanceAlert() {}

    public SurveillanceAlert(String district, String disease, String riskLevel, Double probability, String message, String triggeringFactors, String recommendedAction) {
        this.district = district;
        this.disease = disease;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.message = message;
        this.triggeringFactors = triggeringFactors;
        this.recommendedAction = recommendedAction;
        this.status = "UNREAD";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTriggeringFactors() { return triggeringFactors; }
    public void setTriggeringFactors(String triggeringFactors) { this.triggeringFactors = triggeringFactors; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

