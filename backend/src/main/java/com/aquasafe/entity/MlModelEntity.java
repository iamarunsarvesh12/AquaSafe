package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ml_models")
public class MlModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "model_type", nullable = false, length = 50)
    private String modelType; // RISK_CLASSIFICATION, DISEASE_SPECIFIC, TIME_SERIES_FORECAST

    @Column(name = "model_version", nullable = false, unique = true, length = 50)
    private String modelVersion;

    @Column(nullable = false, length = 100)
    private String algorithm; // Random Forest, XGBoost, Gradient Boosting, Holt-Winters

    @Column(name = "target_variable", nullable = false, length = 100)
    private String targetVariable;

    @Column(name = "training_date")
    private LocalDateTime trainingDate = LocalDateTime.now();

    @Column(name = "total_training_samples", nullable = false)
    private Integer totalTrainingSamples;

    @Column(nullable = false)
    private Double accuracy;

    @Column(name = "precision_score", nullable = false)
    private Double precisionScore;

    @Column(name = "recall_score", nullable = false)
    private Double recallScore;

    @Column(name = "f1_score", nullable = false)
    private Double f1Score;

    @Column(name = "roc_auc")
    private Double rocAuc;

    private Boolean active = true;

    @Column(name = "file_path", length = 255)
    private String filePath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MlModelEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getTargetVariable() { return targetVariable; }
    public void setTargetVariable(String targetVariable) { this.targetVariable = targetVariable; }

    public LocalDateTime getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; }

    public Integer getTotalTrainingSamples() { return totalTrainingSamples; }
    public void setTotalTrainingSamples(Integer totalTrainingSamples) { this.totalTrainingSamples = totalTrainingSamples; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getPrecisionScore() { return precisionScore; }
    public void setPrecisionScore(Double precisionScore) { this.precisionScore = precisionScore; }

    public Double getRecallScore() { return recallScore; }
    public void setRecallScore(Double recallScore) { this.recallScore = recallScore; }

    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }

    public Double getRocAuc() { return rocAuc; }
    public void setRocAuc(Double rocAuc) { this.rocAuc = rocAuc; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

