package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_quality_reports")
public class DataQualityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @Column(name = "check_date")
    private LocalDateTime checkDate = LocalDateTime.now();

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords;

    @Column(name = "completeness_pct", nullable = false)
    private Double completenessPct;

    @Column(name = "duplicate_count", nullable = false)
    private Integer duplicateCount = 0;

    @Column(name = "outlier_count", nullable = false)
    private Integer outlierCount = 0;

    @Column(name = "missing_values_count", nullable = false)
    private Integer missingValuesCount = 0;

    @Column(name = "validity_status", nullable = false, length = 50)
    private String validityStatus = "PASSED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DataQualityReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }

    public LocalDateTime getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDateTime checkDate) { this.checkDate = checkDate; }

    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }

    public Double getCompletenessPct() { return completenessPct; }
    public void setCompletenessPct(Double completenessPct) { this.completenessPct = completenessPct; }

    public Integer getDuplicateCount() { return duplicateCount; }
    public void setDuplicateCount(Integer duplicateCount) { this.duplicateCount = duplicateCount; }

    public Integer getOutlierCount() { return outlierCount; }
    public void setOutlierCount(Integer outlierCount) { this.outlierCount = outlierCount; }

    public Integer getMissingValuesCount() { return missingValuesCount; }
    public void setMissingValuesCount(Integer missingValuesCount) { this.missingValuesCount = missingValuesCount; }

    public String getValidityStatus() { return validityStatus; }
    public void setValidityStatus(String validityStatus) { this.validityStatus = validityStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

