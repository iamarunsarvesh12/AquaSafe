package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_quality", indexes = {
    @Index(name = "idx_wq_district", columnList = "district"),
    @Index(name = "idx_wq_date", columnList = "date"),
    @Index(name = "idx_wq_district_date", columnList = "district, date")
})
public class WaterQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state = "Tamil Nadu";

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double ph;

    @Column(nullable = false)
    private Double turbidity;

    @Column(nullable = false)
    private Double tds;

    @Column(name = "dissolved_oxygen", nullable = false)
    private Double dissolvedOxygen;

    @Column(name = "water_temperature", nullable = false)
    private Double waterTemperature;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WaterQuality() {}

    public WaterQuality(String district, String state, LocalDate date, Double ph, Double turbidity, Double tds, Double dissolvedOxygen, Double waterTemperature) {
        this.district = district;
        this.state = state;
        this.date = date;
        this.ph = ph;
        this.turbidity = turbidity;
        this.tds = tds;
        this.dissolvedOxygen = dissolvedOxygen;
        this.waterTemperature = waterTemperature;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public Double getTds() { return tds; }
    public void setTds(Double tds) { this.tds = tds; }

    public Double getDissolvedOxygen() { return dissolvedOxygen; }
    public void setDissolvedOxygen(Double dissolvedOxygen) { this.dissolvedOxygen = dissolvedOxygen; }

    public Double getWaterTemperature() { return waterTemperature; }
    public void setWaterTemperature(Double waterTemperature) { this.waterTemperature = waterTemperature; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
