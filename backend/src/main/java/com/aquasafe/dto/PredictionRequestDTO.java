package com.aquasafe.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PredictionRequestDTO {

    @NotBlank(message = "District is required")
    private String district;

    @NotNull(message = "pH is required")
    @DecimalMin(value = "0.0", message = "pH cannot be negative")
    @DecimalMax(value = "14.0", message = "pH cannot exceed 14.0")
    private Double ph;

    @NotNull(message = "Turbidity is required")
    @DecimalMin(value = "0.0", message = "Turbidity cannot be negative")
    private Double turbidity;

    @NotNull(message = "TDS is required")
    @DecimalMin(value = "0.0", message = "TDS cannot be negative")
    private Double tds;

    @NotNull(message = "Dissolved Oxygen is required")
    @DecimalMin(value = "0.0", message = "Dissolved oxygen cannot be negative")
    private Double dissolvedOxygen;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "Rainfall is required")
    @DecimalMin(value = "0.0", message = "Rainfall cannot be negative")
    private Double rainfall;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity cannot be negative")
    @DecimalMax(value = "100.0", message = "Humidity cannot exceed 100%")
    private Double humidity;

    @NotNull(message = "Previous disease cases is required")
    private Integer previousDiseaseCases;

    @NotNull(message = "Population density is required")
    private Integer populationDensity;

    @NotNull(message = "Sanitation coverage is required")
    @DecimalMin(value = "0.0", message = "Sanitation coverage cannot be negative")
    @DecimalMax(value = "100.0", message = "Sanitation coverage cannot exceed 100%")
    private Double sanitationCoverage;

    public PredictionRequestDTO() {}

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

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
