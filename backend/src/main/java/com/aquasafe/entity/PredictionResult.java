package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_results", indexes = {
    @Index(name = "idx_pred_district", columnList = "district"),
    @Index(name = "idx_pred_date", columnList = "predictionDate"),
    @Index(name = "idx_pred_risk", columnList = "riskLevel")
})
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "prediction_date", nullable = false)
    private LocalDateTime predictionDate = LocalDateTime.now();

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    private Double probability;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion = "RandomForest-v1.0";

    // Feature Snapshot
    private Double ph;
    private Double turbidity;
    private Double tds;

    @Column(name = "dissolved_oxygen")
    private Double dissolvedOxygen;

    private Double temperature;
    private Double rainfall;
    private Double humidity;

    @Column(name = "previous_disease_cases")
    private Integer previousDiseaseCases;

    @Column(name = "population_density")
    private Integer populationDensity;

    @Column(name = "sanitation_coverage")
    private Double sanitationCoverage;

    public PredictionResult() {}

    public PredictionResult(String district, String riskLevel, Double probability, String modelVersion) {
        this.district = district;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.modelVersion = modelVersion;
        this.predictionDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public Double getTds() { return tds; }
    public void setTds(Double tds) { this.tds = tds; }

    public Double getDissolvedOxygen() { return dissolvedOxygen; }
    public void setDissolvedOxygen(Double dissolvedOxygen) { this.dissolvedOxygen = dissolvedOxygen; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Integer getPreviousDiseaseCases() { return previousDiseaseCases; }
    public void setPreviousDiseaseCases(Integer previousDiseaseCases) { this.previousDiseaseCases = previousDiseaseCases; }

    public Integer getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(Integer populationDensity) { this.populationDensity = populationDensity; }

    public Double getSanitationCoverage() { return sanitationCoverage; }
    public void setSanitationCoverage(Double sanitationCoverage) { this.sanitationCoverage = sanitationCoverage; }
}
