package com.aquasafe.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ModelEvaluationDTO {

    private boolean trained;
    private String modelName = "Weka Random Forest";
    private String modelVersion = "RandomForest-v1.0";
    private int numberOfTrees = 100;
    private LocalDateTime trainedDate;
    private int totalInstances;

    // Actual Calculated Metrics
    private Double accuracy; // in percentage e.g. 91.25%
    private Double precision;
    private Double recall;
    private Double f1Score;

    // Class wise metrics
    private Map<String, Map<String, Double>> classMetrics;

    // Confusion Matrix: [[LOW_LOW, LOW_MED, LOW_HIGH], ...]
    private List<List<Integer>> confusionMatrix;
    private List<String> classNames;

    // Cross Validation summary string from Weka
    private String crossValidationSummary;
    private String datasetStatus; // "Trained on demonstration dataset (240 instances)"
    private String notes;

    public ModelEvaluationDTO() {}

    public boolean isTrained() { return trained; }
    public void setTrained(boolean trained) { this.trained = trained; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public int getNumberOfTrees() { return numberOfTrees; }
    public void setNumberOfTrees(int numberOfTrees) { this.numberOfTrees = numberOfTrees; }

    public LocalDateTime getTrainedDate() { return trainedDate; }
    public void setTrainedDate(LocalDateTime trainedDate) { this.trainedDate = trainedDate; }

    public int getTotalInstances() { return totalInstances; }
    public void setTotalInstances(int totalInstances) { this.totalInstances = totalInstances; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getPrecision() { return precision; }
    public void setPrecision(Double precision) { this.precision = precision; }

    public Double getRecall() { return recall; }
    public void setRecall(Double recall) { this.recall = recall; }

    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }

    public Map<String, Map<String, Double>> getClassMetrics() { return classMetrics; }
    public void setClassMetrics(Map<String, Map<String, Double>> classMetrics) { this.classMetrics = classMetrics; }

    public List<List<Integer>> getConfusionMatrix() { return confusionMatrix; }
    public void setConfusionMatrix(List<List<Integer>> confusionMatrix) { this.confusionMatrix = confusionMatrix; }

    public List<String> getClassNames() { return classNames; }
    public void setClassNames(List<String> classNames) { this.classNames = classNames; }

    public String getCrossValidationSummary() { return crossValidationSummary; }
    public void setCrossValidationSummary(String crossValidationSummary) { this.crossValidationSummary = crossValidationSummary; }

    public String getDatasetStatus() { return datasetStatus; }
    public void setDatasetStatus(String datasetStatus) { this.datasetStatus = datasetStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
