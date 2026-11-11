package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "population_records", indexes = {
    @Index(name = "idx_pop_dist_year", columnList = "districtName, year")
})
public class PopulationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "district_code", length = 20)
    private String districtCode;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_population", nullable = false)
    private Long totalPopulation;

    @Column(name = "population_density", nullable = false)
    private Integer populationDensity;

    @Column(name = "urban_population")
    private Long urbanPopulation;

    @Column(name = "rural_population")
    private Long ruralPopulation;

    @Column(name = "sanitation_coverage", nullable = false)
    private Double sanitationCoverage;

    @Column(length = 100)
    private String source = "CENSUS_INDIA_2011_PROJECTED";

    @Column(name = "is_estimated")
    private Boolean isEstimated = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PopulationRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Long getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(Long totalPopulation) { this.totalPopulation = totalPopulation; }

    public Integer getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(Integer populationDensity) { this.populationDensity = populationDensity; }

    public Long getUrbanPopulation() { return urbanPopulation; }
    public void setUrbanPopulation(Long urbanPopulation) { this.urbanPopulation = urbanPopulation; }

    public Long getRuralPopulation() { return ruralPopulation; }
    public void setRuralPopulation(Long ruralPopulation) { this.ruralPopulation = ruralPopulation; }

    public Double getSanitationCoverage() { return sanitationCoverage; }
    public void setSanitationCoverage(Double sanitationCoverage) { this.sanitationCoverage = sanitationCoverage; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Boolean getIsEstimated() { return isEstimated; }
    public void setIsEstimated(Boolean isEstimated) { this.isEstimated = isEstimated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

