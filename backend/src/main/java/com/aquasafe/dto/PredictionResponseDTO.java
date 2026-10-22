package com.aquasafe.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class PredictionResponseDTO {

    private String district;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private Double probability;
    private LocalDateTime predictionDate;
    private String modelVersion;
    private Map<String, Double> classProbabilities;
    private String message;
    private String disclaimer = "Prediction indicates estimated risk based on available project data. It is not a medical diagnosis or confirmation of an outbreak.";

    public PredictionResponseDTO() {}

    public PredictionResponseDTO(String district, String riskLevel, Double probability, LocalDateTime predictionDate, String modelVersion, Map<String, Double> classProbabilities, String message) {
        this.district = district;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.predictionDate = predictionDate;
        this.modelVersion = modelVersion;
        this.classProbabilities = classProbabilities;
        this.message = message;
    }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public Map<String, Double> getClassProbabilities() { return classProbabilities; }
    public void setClassProbabilities(Map<String, Double> classProbabilities) { this.classProbabilities = classProbabilities; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
