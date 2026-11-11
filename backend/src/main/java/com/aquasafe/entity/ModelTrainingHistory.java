package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "model_training_history")
public class ModelTrainingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    @Column(name = "training_timestamp")
    private LocalDateTime trainingTimestamp = LocalDateTime.now();

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @Column(name = "instances_count", nullable = false)
    private Integer instancesCount;

    @Column(name = "folds_count", nullable = false)
    private Integer foldsCount = 10;

    @Column(nullable = false)
    private Double accuracy;

    @Column(name = "f1_score", nullable = false)
    private Double f1Score;

    @Column(name = "training_duration_ms", nullable = false)
    private Long trainingDurationMs;

    @Column(nullable = false, length = 50)
    private String status = "SUCCESS";

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ModelTrainingHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public LocalDateTime getTrainingTimestamp() { return trainingTimestamp; }
    public void setTrainingTimestamp(LocalDateTime trainingTimestamp) { this.trainingTimestamp = trainingTimestamp; }

    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }

    public Integer getInstancesCount() { return instancesCount; }
    public void setInstancesCount(Integer instancesCount) { this.instancesCount = instancesCount; }

    public Integer getFoldsCount() { return foldsCount; }
    public void setFoldsCount(Integer foldsCount) { this.foldsCount = foldsCount; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }

    public Long getTrainingDurationMs() { return trainingDurationMs; }
    public void setTrainingDurationMs(Long trainingDurationMs) { this.trainingDurationMs = trainingDurationMs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

