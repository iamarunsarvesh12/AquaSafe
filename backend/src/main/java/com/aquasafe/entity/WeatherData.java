package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data", indexes = {
    @Index(name = "idx_weather_district", columnList = "district"),
    @Index(name = "idx_weather_date", columnList = "date"),
    @Index(name = "idx_weather_district_date", columnList = "district, date")
})
public class WeatherData {

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
    private Double temperature;

    @Column(nullable = false)
    private Double rainfall;

    @Column(nullable = false)
    private Double humidity;

    @Column(length = 50)
    private String source = "STORED_DATA";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WeatherData() {}

    public WeatherData(String district, String state, LocalDate date, Double temperature, Double rainfall, Double humidity, String source) {
        this.district = district;
        this.state = state;
        this.date = date;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.humidity = humidity;
        this.source = source != null ? source : "STORED_DATA";
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

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
