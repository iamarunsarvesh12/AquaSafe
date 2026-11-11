package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "districts", indexes = {
    @Index(name = "idx_dist_name", columnList = "districtName"),
    @Index(name = "idx_dist_code", columnList = "districtCode")
})
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_id", nullable = false, unique = true)
    private Integer districtId;

    @Column(name = "district_code", nullable = false, unique = true, length = 20)
    private String districtCode;

    @Column(name = "district_name", nullable = false, unique = true, length = 100)
    private String districtName;

    @Column(length = 50)
    private String state = "Tamil Nadu";

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "area_sq_km", nullable = false)
    private Double areaSqKm;

    @Column(name = "census_2011_population", nullable = false)
    private Long census2011Population;

    @Column(name = "population_2025", nullable = false)
    private Long population2025;

    @Column(name = "population_density", nullable = false)
    private Integer populationDensity;

    @Column(name = "urban_population_pct", nullable = false)
    private Double urbanPopulationPct;

    @Column(name = "rural_population_pct", nullable = false)
    private Double ruralPopulationPct;

    @Column(name = "sanitation_coverage", nullable = false)
    private Double sanitationCoverage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public District() {}

    public District(Integer districtId, String districtCode, String districtName, Double latitude, Double longitude,
                    Double areaSqKm, Long census2011Population, Long population2025, Integer populationDensity,
                    Double urbanPopulationPct, Double ruralPopulationPct, Double sanitationCoverage) {
        this.districtId = districtId;
        this.districtCode = districtCode;
        this.districtName = districtName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaSqKm = areaSqKm;
        this.census2011Population = census2011Population;
        this.population2025 = population2025;
        this.populationDensity = populationDensity;
        this.urbanPopulationPct = urbanPopulationPct;
        this.ruralPopulationPct = ruralPopulationPct;
        this.sanitationCoverage = sanitationCoverage;
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

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getAreaSqKm() { return areaSqKm; }
    public void setAreaSqKm(Double areaSqKm) { this.areaSqKm = areaSqKm; }

    public Long getCensus2011Population() { return census2011Population; }
    public void setCensus2011Population(Long census2011Population) { this.census2011Population = census2011Population; }

    public Long getPopulation2025() { return population2025; }
    public void setPopulation2025(Long population2025) { this.population2025 = population2025; }

    public Integer getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(Integer populationDensity) { this.populationDensity = populationDensity; }

    public Double getUrbanPopulationPct() { return urbanPopulationPct; }
    public void setUrbanPopulationPct(Double urbanPopulationPct) { this.urbanPopulationPct = urbanPopulationPct; }

    public Double getRuralPopulationPct() { return ruralPopulationPct; }
    public void setRuralPopulationPct(Double ruralPopulationPct) { this.ruralPopulationPct = ruralPopulationPct; }

    public Double getSanitationCoverage() { return sanitationCoverage; }
    public void setSanitationCoverage(Double sanitationCoverage) { this.sanitationCoverage = sanitationCoverage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

