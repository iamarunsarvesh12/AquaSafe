package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_records", indexes = {
    @Index(name = "idx_weather_dist", columnList = "districtName"),
    @Index(name = "idx_weather_date", columnList = "observationDate"),
    @Index(name = "idx_weather_dist_date", columnList = "districtName, observationDate")
})
public class WeatherRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_code", length = 20)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "temp_min")
    private Double tempMin;

    @Column(name = "temp_max")
    private Double tempMax;

    @Column(name = "temp_avg", nullable = false)
    private Double tempAvg;

    @Column(nullable = false)
    private Double rainfall;

    @Column(nullable = false)
    private Double humidity;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "source_attribution", length = 100)
    private String sourceAttribution = "IMD_HISTORICAL_GRIDDED";

    @Column(name = "observation_timestamp", length = 50)
    private String observationTimestamp;

    @Column(name = "data_type", length = 50)
    private String dataType = "HISTORICAL_OBSERVED"; // HISTORICAL_OBSERVED, CURRENT_OBSERVED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WeatherRecord() {}

    public WeatherRecord(Integer districtId, String districtCode, String districtName, LocalDate observationDate,
                         Double tempMin, Double tempMax, Double tempAvg, Double rainfall, Double humidity,
                         Double windSpeed, String sourceAttribution) {
        this.districtId = districtId;
        this.districtCode = districtCode;
        this.districtName = districtName;
        this.observationDate = observationDate;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.tempAvg = tempAvg;
        this.rainfall = rainfall;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.sourceAttribution = sourceAttribution;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public LocalDate getObservationDate() { return observationDate; }
    public void setObservationDate(LocalDate observationDate) { this.observationDate = observationDate; }

    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }

    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }

    public Double getTempAvg() { return tempAvg; }
    public void setTempAvg(Double tempAvg) { this.tempAvg = tempAvg; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public String getSourceAttribution() { return sourceAttribution; }
    public void setSourceAttribution(String sourceAttribution) { this.sourceAttribution = sourceAttribution; }

    public String getObservationTimestamp() { return observationTimestamp; }
    public void setObservationTimestamp(String observationTimestamp) { this.observationTimestamp = observationTimestamp; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

