package com.aquasafe.spark;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DataProcessingResult {

    private String engineName;
    private long datasetSize;
    private int recordsProcessed;
    private int districtCount;
    private long processingTimeMs;
    private String status;
    private Map<String, Map<String, Object>> districtAggregates;
    private Map<String, Long> monthlyDiseaseTrends;
    private Map<String, Double> waterQualityAverages;
    private Map<String, Double> weatherAverages;
    private LocalDateTime executedAt = LocalDateTime.now();

    public DataProcessingResult() {}

    public String getEngineName() { return engineName; }
    public void setEngineName(String engineName) { this.engineName = engineName; }

    public long getDatasetSize() { return datasetSize; }
    public void setDatasetSize(long datasetSize) { this.datasetSize = datasetSize; }

    public int getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }

    public int getDistrictCount() { return districtCount; }
    public void setDistrictCount(int districtCount) { this.districtCount = districtCount; }

    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Map<String, Object>> getDistrictAggregates() { return districtAggregates; }
    public void setDistrictAggregates(Map<String, Map<String, Object>> districtAggregates) { this.districtAggregates = districtAggregates; }

    public Map<String, Long> getMonthlyDiseaseTrends() { return monthlyDiseaseTrends; }
    public void setMonthlyDiseaseTrends(Map<String, Long> monthlyDiseaseTrends) { this.monthlyDiseaseTrends = monthlyDiseaseTrends; }

    public Map<String, Double> getWaterQualityAverages() { return waterQualityAverages; }
    public void setWaterQualityAverages(Map<String, Double> waterQualityAverages) { this.waterQualityAverages = waterQualityAverages; }

    public Map<String, Double> getWeatherAverages() { return weatherAverages; }
    public void setWeatherAverages(Map<String, Double> weatherAverages) { this.weatherAverages = weatherAverages; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
