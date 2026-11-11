package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_sources")
public class DatasetSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", unique = true)
    private Integer sourceId;

    @Column(name = "source_name", nullable = false, length = 150)
    private String sourceName;

    @Column(name = "source_url", nullable = false, length = 255)
    private String sourceUrl;

    @Column(name = "dataset_category", nullable = false, length = 50)
    private String datasetCategory;

    @Column(name = "download_date")
    private LocalDate downloadDate;

    @Column(name = "observation_period", length = 100)
    private String observationPeriod;

    @Column(name = "district_coverage", length = 100)
    private String districtCoverage;

    @Column(name = "record_count")
    private Integer recordCount = 0;

    @Column(name = "data_format", length = 50)
    private String dataFormat;

    @Column(name = "validation_status", length = 50)
    private String validationStatus = "VALIDATED";

    @Column(name = "data_classification", length = 100)
    private String dataClassification;

    @Column(name = "access_restrictions", columnDefinition = "TEXT")
    private String accessRestrictions;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DatasetSource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSourceId() { return sourceId; }
    public void setSourceId(Integer sourceId) { this.sourceId = sourceId; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getDatasetCategory() { return datasetCategory; }
    public void setDatasetCategory(String datasetCategory) { this.datasetCategory = datasetCategory; }

    public LocalDate getDownloadDate() { return downloadDate; }
    public void setDownloadDate(LocalDate downloadDate) { this.downloadDate = downloadDate; }

    public String getObservationPeriod() { return observationPeriod; }
    public void setObservationPeriod(String observationPeriod) { this.observationPeriod = observationPeriod; }

    public String getDistrictCoverage() { return districtCoverage; }
    public void setDistrictCoverage(String districtCoverage) { this.districtCoverage = districtCoverage; }

    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }

    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String dataFormat) { this.dataFormat = dataFormat; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public String getDataClassification() { return dataClassification; }
    public void setDataClassification(String dataClassification) { this.dataClassification = dataClassification; }

    public String getAccessRestrictions() { return accessRestrictions; }
    public void setAccessRestrictions(String accessRestrictions) { this.accessRestrictions = accessRestrictions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

