package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_analytics", indexes = {
    @Index(name = "idx_pa_dist_date", columnList = "districtName, analysisDate")
})
public class ProcessedAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "avg_ph")
    private Double avgPh;

    @Column(name = "avg_turbidity")
    private Double avgTurbidity;

    @Column(name = "avg_tds")
    private Double avgTds;

    @Column(name = "avg_do")
    private Double avgDo;

    @Column(name = "avg_temperature")
    private Double avgTemperature;

    @Column(name = "total_rainfall")
    private Double totalRainfall;

    @Column(name = "avg_humidity")
    private Double avgHumidity;

    @Column(name = "total_disease_cases")
    private Integer totalDiseaseCases;

    @Column(name = "disease_incidence_rate")
    private Double diseaseIncidenceRate;

    @Column(name = "calculated_wqi")
    private Double calculatedWqi;

    @Column(name = "risk_classification", length = 20)
    private String riskClassification; // LOW, MEDIUM, HIGH

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProcessedAnalytics() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }

    public Double getAvgPh() { return avgPh; }
    public void setAvgPh(Double avgPh) { this.avgPh = avgPh; }

    public Double getAvgTurbidity() { return avgTurbidity; }
    public void setAvgTurbidity(Double avgTurbidity) { this.avgTurbidity = avgTurbidity; }

    public Double getAvgTds() { return avgTds; }
    public void setAvgTds(Double avgTds) { this.avgTds = avgTds; }

    public Double getAvgDo() { return avgDo; }
    public void setAvgDo(Double avgDo) { this.avgDo = avgDo; }

    public Double getAvgTemperature() { return avgTemperature; }
    public void setAvgTemperature(Double avgTemperature) { this.avgTemperature = avgTemperature; }

    public Double getTotalRainfall() { return totalRainfall; }
    public void setTotalRainfall(Double totalRainfall) { this.totalRainfall = totalRainfall; }

    public Double getAvgHumidity() { return avgHumidity; }
    public void setAvgHumidity(Double avgHumidity) { this.avgHumidity = avgHumidity; }

    public Integer getTotalDiseaseCases() { return totalDiseaseCases; }
    public void setTotalDiseaseCases(Integer totalDiseaseCases) { this.totalDiseaseCases = totalDiseaseCases; }

    public Double getDiseaseIncidenceRate() { return diseaseIncidenceRate; }
    public void setDiseaseIncidenceRate(Double diseaseIncidenceRate) { this.diseaseIncidenceRate = diseaseIncidenceRate; }

    public Double getCalculatedWqi() { return calculatedWqi; }
    public void setCalculatedWqi(Double calculatedWqi) { this.calculatedWqi = calculatedWqi; }

    public String getRiskClassification() { return riskClassification; }
    public void setRiskClassification(String riskClassification) { this.riskClassification = riskClassification; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

