package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disease_forecasts", indexes = {
    @Index(name = "idx_fc_dist_disease", columnList = "district, diseaseName")
})
public class DiseaseForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "disease_name", nullable = false, length = 100)
    private String diseaseName;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "horizon_days", nullable = false)
    private Integer horizonDays; // 7 or 30

    @Column(name = "forecasted_cases", nullable = false)
    private Integer forecastedCases;

    @Column(name = "lower_confidence", nullable = false)
    private Integer lowerConfidence;

    @Column(name = "upper_confidence", nullable = false)
    private Integer upperConfidence;

    @Column(name = "model_used", nullable = false, length = 100)
    private String modelUsed = "Holt-Winters Exponential Smoothing";

    private Double mae;
    private Double rmse;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DiseaseForecast() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }

    public Integer getHorizonDays() { return horizonDays; }
    public void setHorizonDays(Integer horizonDays) { this.horizonDays = horizonDays; }

    public Integer getForecastedCases() { return forecastedCases; }
    public void setForecastedCases(Integer forecastedCases) { this.forecastedCases = forecastedCases; }

    public Integer getLowerConfidence() { return lowerConfidence; }
    public void setLowerConfidence(Integer lowerConfidence) { this.lowerConfidence = lowerConfidence; }

    public Integer getUpperConfidence() { return upperConfidence; }
    public void setUpperConfidence(Integer upperConfidence) { this.upperConfidence = upperConfidence; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public Double getMae() { return mae; }
    public void setMae(Double mae) { this.mae = mae; }

    public Double getRmse() { return rmse; }
    public void setRmse(Double rmse) { this.rmse = rmse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

