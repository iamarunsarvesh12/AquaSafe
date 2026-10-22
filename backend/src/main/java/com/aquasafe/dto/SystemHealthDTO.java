package com.aquasafe.dto;

import java.time.LocalDateTime;

public class SystemHealthDTO {

    private String backendStatus;    // ONLINE
    private String databaseType;     // MySQL or H2 (MySQL Mode)
    private String databaseStatus;   // ONLINE, OFFLINE
    private String mlModelStatus;    // ONLINE (Trained), NOT TRAINED
    private String mlModelVersion;
    private String weatherApiStatus; // ONLINE (Live API), STORED DATA (Graceful fallback)
    private String weatherApiProvider;
    private String sparkStatus;      // ONLINE (Cluster active), LOCAL MODE (Fallback engine)
    private String hadoopStatus;     // ONLINE (HDFS active), LOCAL STORAGE (Fallback)
    private LocalDateTime timestamp = LocalDateTime.now();

    public SystemHealthDTO() {}

    public String getBackendStatus() { return backendStatus; }
    public void setBackendStatus(String backendStatus) { this.backendStatus = backendStatus; }

    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }

    public String getDatabaseStatus() { return databaseStatus; }
    public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }

    public String getMlModelStatus() { return mlModelStatus; }
    public void setMlModelStatus(String mlModelStatus) { this.mlModelStatus = mlModelStatus; }

    public String getMlModelVersion() { return mlModelVersion; }
    public void setMlModelVersion(String mlModelVersion) { this.mlModelVersion = mlModelVersion; }

    public String getWeatherApiStatus() { return weatherApiStatus; }
    public void setWeatherApiStatus(String weatherApiStatus) { this.weatherApiStatus = weatherApiStatus; }

    public String getWeatherApiProvider() { return weatherApiProvider; }
    public void setWeatherApiProvider(String weatherApiProvider) { this.weatherApiProvider = weatherApiProvider; }

    public String getSparkStatus() { return sparkStatus; }
    public void setSparkStatus(String sparkStatus) { this.sparkStatus = sparkStatus; }

    public String getHadoopStatus() { return hadoopStatus; }
    public void setHadoopStatus(String hadoopStatus) { this.hadoopStatus = hadoopStatus; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
