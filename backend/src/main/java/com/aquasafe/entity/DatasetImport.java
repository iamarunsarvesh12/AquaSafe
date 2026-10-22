package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_imports")
public class DatasetImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "dataset_type", nullable = false, length = 50)
    private String datasetType;

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords = 0;

    @Column(name = "valid_records", nullable = false)
    private Integer validRecords = 0;

    @Column(name = "invalid_records", nullable = false)
    private Integer invalidRecords = 0;

    @Column(name = "duplicate_records", nullable = false)
    private Integer duplicateRecords = 0;

    @Column(name = "missing_values", nullable = false)
    private Integer missingValues = 0;

    @Column(nullable = false, length = 50)
    private String status = "SUCCESS";

    @Column(name = "import_time")
    private LocalDateTime importTime = LocalDateTime.now();

    public DatasetImport() {}

    public DatasetImport(String filename, String datasetType, Integer totalRecords, Integer validRecords, Integer invalidRecords, Integer duplicateRecords, Integer missingValues, String status) {
        this.filename = filename;
        this.datasetType = datasetType;
        this.totalRecords = totalRecords;
        this.validRecords = validRecords;
        this.invalidRecords = invalidRecords;
        this.duplicateRecords = duplicateRecords;
        this.missingValues = missingValues;
        this.status = status;
        this.importTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getDatasetType() { return datasetType; }
    public void setDatasetType(String datasetType) { this.datasetType = datasetType; }

    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }

    public Integer getValidRecords() { return validRecords; }
    public void setValidRecords(Integer validRecords) { this.validRecords = validRecords; }

    public Integer getInvalidRecords() { return invalidRecords; }
    public void setInvalidRecords(Integer invalidRecords) { this.invalidRecords = invalidRecords; }

    public Integer getDuplicateRecords() { return duplicateRecords; }
    public void setDuplicateRecords(Integer duplicateRecords) { this.duplicateRecords = duplicateRecords; }

    public Integer getMissingValues() { return missingValues; }
    public void setMissingValues(Integer missingValues) { this.missingValues = missingValues; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getImportTime() { return importTime; }
    public void setImportTime(LocalDateTime importTime) { this.importTime = importTime; }
}
