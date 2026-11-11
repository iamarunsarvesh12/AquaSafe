package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spark_job_history")
public class SparkJobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 150)
    private String jobName;

    @Column(name = "spark_master", nullable = false, length = 100)
    private String sparkMaster;

    @Column(name = "input_records", nullable = false)
    private Integer inputRecords;

    @Column(name = "processed_records", nullable = false)
    private Integer processedRecords;

    @Column(name = "rejected_records", nullable = false)
    private Integer rejectedRecords = 0;

    @Column(name = "partitions_count", nullable = false)
    private Integer partitionsCount;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    @Column(name = "dataset_size_bytes", nullable = false)
    private Long datasetSizeBytes;

    @Column(name = "parquet_path", length = 255)
    private String parquetPath;

    @Column(nullable = false, length = 50)
    private String status = "SUCCESS";

    @Column(name = "execution_timestamp")
    private LocalDateTime executionTimestamp = LocalDateTime.now();

    public SparkJobHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getSparkMaster() { return sparkMaster; }
    public void setSparkMaster(String sparkMaster) { this.sparkMaster = sparkMaster; }

    public Integer getInputRecords() { return inputRecords; }
    public void setInputRecords(Integer inputRecords) { this.inputRecords = inputRecords; }

    public Integer getProcessedRecords() { return processedRecords; }
    public void setProcessedRecords(Integer processedRecords) { this.processedRecords = processedRecords; }

    public Integer getRejectedRecords() { return rejectedRecords; }
    public void setRejectedRecords(Integer rejectedRecords) { this.rejectedRecords = rejectedRecords; }

    public Integer getPartitionsCount() { return partitionsCount; }
    public void setPartitionsCount(Integer partitionsCount) { this.partitionsCount = partitionsCount; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public Long getDatasetSizeBytes() { return datasetSizeBytes; }
    public void setDatasetSizeBytes(Long datasetSizeBytes) { this.datasetSizeBytes = datasetSizeBytes; }

    public String getParquetPath() { return parquetPath; }
    public void setParquetPath(String parquetPath) { this.parquetPath = parquetPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExecutionTimestamp() { return executionTimestamp; }
    public void setExecutionTimestamp(LocalDateTime executionTimestamp) { this.executionTimestamp = executionTimestamp; }
}

