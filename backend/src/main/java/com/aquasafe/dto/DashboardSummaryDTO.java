package com.aquasafe.dto;

import java.time.LocalDateTime;

public class DashboardSummaryDTO {

    private long totalRecords;
    private long totalDistricts;
    private long highRiskDistricts;
    private long mediumRiskDistricts;
    private long lowRiskDistricts;
    private long latestPredictionsCount;
    private long activeAlertsCount;
    private String dataFreshness; // LIVE DATA, STORED DATA, DEMO DATA
    private LocalDateTime lastUpdated;
    private String weatherSource;

    public DashboardSummaryDTO() {}

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

    public long getTotalDistricts() { return totalDistricts; }
    public void setTotalDistricts(long totalDistricts) { this.totalDistricts = totalDistricts; }

    public long getHighRiskDistricts() { return highRiskDistricts; }
    public void setHighRiskDistricts(long highRiskDistricts) { this.highRiskDistricts = highRiskDistricts; }

    public long getMediumRiskDistricts() { return mediumRiskDistricts; }
    public void setMediumRiskDistricts(long mediumRiskDistricts) { this.mediumRiskDistricts = mediumRiskDistricts; }

    public long getLowRiskDistricts() { return lowRiskDistricts; }
    public void setLowRiskDistricts(long lowRiskDistricts) { this.lowRiskDistricts = lowRiskDistricts; }

    public long getLatestPredictionsCount() { return latestPredictionsCount; }
    public void setLatestPredictionsCount(long latestPredictionsCount) { this.latestPredictionsCount = latestPredictionsCount; }

    public long getActiveAlertsCount() { return activeAlertsCount; }
    public void setActiveAlertsCount(long activeAlertsCount) { this.activeAlertsCount = activeAlertsCount; }

    public String getDataFreshness() { return dataFreshness; }
    public void setDataFreshness(String dataFreshness) { this.dataFreshness = dataFreshness; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getWeatherSource() { return weatherSource; }
    public void setWeatherSource(String weatherSource) { this.weatherSource = weatherSource; }
}
