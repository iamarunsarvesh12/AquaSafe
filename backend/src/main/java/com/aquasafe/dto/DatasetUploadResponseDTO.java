package com.aquasafe.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DatasetUploadResponseDTO {

    private String filename;
    private String datasetType;
    private int totalRecords;
    private int validRecords;
    private int invalidRecords;
    private int duplicateRecords;
    private int missingValues;
    private String status;
    private List<Map<String, String>> previewRows;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public DatasetUploadResponseDTO() {}

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getDatasetType() { return datasetType; }
    public void setDatasetType(String datasetType) { this.datasetType = datasetType; }

    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }

    public int getValidRecords() { return validRecords; }
    public void setValidRecords(int validRecords) { this.validRecords = validRecords; }

    public int getInvalidRecords() { return invalidRecords; }
    public void setInvalidRecords(int invalidRecords) { this.invalidRecords = invalidRecords; }

    public int getDuplicateRecords() { return duplicateRecords; }
    public void setDuplicateRecords(int duplicateRecords) { this.duplicateRecords = duplicateRecords; }

    public int getMissingValues() { return missingValues; }
    public void setMissingValues(int missingValues) { this.missingValues = missingValues; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Map<String, String>> getPreviewRows() { return previewRows; }
    public void setPreviewRows(List<Map<String, String>> previewRows) { this.previewRows = previewRows; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
