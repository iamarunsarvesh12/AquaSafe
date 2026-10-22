package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_district", columnList = "district"),
    @Index(name = "idx_alerts_status", columnList = "status"),
    @Index(name = "idx_alerts_created", columnList = "createdAt")
})
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(nullable = false)
    private Double probability;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, length = 20)
    private String status = "UNREAD"; // UNREAD, READ

    public Alert() {}

    public Alert(String district, String riskLevel, Double probability, String message) {
        this.district = district;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.status = "UNREAD";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
