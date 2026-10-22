package com.aquasafe.dto;

import java.time.LocalDateTime;

public class PreprocessingStatsDTO {

    private String datasetType;
    private int recordsBefore;
    private int recordsAfterCleaning;
    private int missingValuesHandled;
    private int duplicatesRemoved;
    private int invalidValuesFiltered;
    private int normalizedFeaturesCount;
    private String status;
    private LocalDateTime executedAt = LocalDateTime.now();

    public PreprocessingStatsDTO() {}

    public PreprocessingStatsDTO(String datasetType, int recordsBefore, int recordsAfterCleaning, int missingValuesHandled, int duplicatesRemoved, int invalidValuesFiltered, int normalizedFeaturesCount, String status) {
        this.datasetType = datasetType;
        this.recordsBefore = recordsBefore;
        this.recordsAfterCleaning = recordsAfterCleaning;
        this.missingValuesHandled = missingValuesHandled;
        this.duplicatesRemoved = duplicatesRemoved;
        this.invalidValuesFiltered = invalidValuesFiltered;
        this.normalizedFeaturesCount = normalizedFeaturesCount;
        this.status = status;
        this.executedAt = LocalDateTime.now();
    }

    public String getDatasetType() { return datasetType; }
    public void setDatasetType(String datasetType) { this.datasetType = datasetType; }

    public int getRecordsBefore() { return recordsBefore; }
    public void setRecordsBefore(int recordsBefore) { this.recordsBefore = recordsBefore; }

    public int getRecordsAfterCleaning() { return recordsAfterCleaning; }
    public void setRecordsAfterCleaning(int recordsAfterCleaning) { this.recordsAfterCleaning = recordsAfterCleaning; }

    public int getMissingValuesHandled() { return missingValuesHandled; }
    public void setMissingValuesHandled(int missingValuesHandled) { this.missingValuesHandled = missingValuesHandled; }

    public int getDuplicatesRemoved() { return duplicatesRemoved; }
    public void setDuplicatesRemoved(int duplicatesRemoved) { this.duplicatesRemoved = duplicatesRemoved; }

    public int getInvalidValuesFiltered() { return invalidValuesFiltered; }
    public void setInvalidValuesFiltered(int invalidValuesFiltered) { this.invalidValuesFiltered = invalidValuesFiltered; }

    public int getNormalizedFeaturesCount() { return normalizedFeaturesCount; }
    public void setNormalizedFeaturesCount(int normalizedFeaturesCount) { this.normalizedFeaturesCount = normalizedFeaturesCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
