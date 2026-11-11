package com.aquasafe.controller;

import com.aquasafe.config.FilePathResolver;
import com.aquasafe.dto.SystemHealthDTO;
import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.SparkJobHistory;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;
import com.aquasafe.repository.DiseaseRecordRepository;
import com.aquasafe.repository.SparkJobHistoryRepository;
import com.aquasafe.repository.WaterQualityRepository;
import com.aquasafe.repository.WeatherDataRepository;
import com.aquasafe.service.DashboardService;
import com.aquasafe.service.DataSyncService;
import com.aquasafe.spark.DataProcessingDispatcher;
import com.aquasafe.spark.DataProcessingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(originPatterns = "*")
public class SystemController {

    private final DashboardService dashboardService;
    private final DataProcessingDispatcher dispatcher;
    private final DataSyncService dataSyncService;
    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;
    private final SparkJobHistoryRepository sparkJobHistoryRepository;

    public SystemController(
            DashboardService dashboardService,
            DataProcessingDispatcher dispatcher,
            DataSyncService dataSyncService,
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository,
            SparkJobHistoryRepository sparkJobHistoryRepository) {
        this.dashboardService = dashboardService;
        this.dispatcher = dispatcher;
        this.dataSyncService = dataSyncService;
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
        this.sparkJobHistoryRepository = sparkJobHistoryRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<SystemHealthDTO> getSystemHealth() {
        return ResponseEntity.ok(dashboardService.getSystemHealth());
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> triggerLiveSync() {
        Map<String, Object> syncResult = dataSyncService.syncAllDistrictsWeather();
        return ResponseEntity.ok(syncResult);
    }

    @GetMapping("/sync-status")
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        return ResponseEntity.ok(dataSyncService.getSyncStatus());
    }

    @PostMapping("/spark/process")
    public ResponseEntity<DataProcessingResult> triggerBigDataProcessing() {
        List<WaterQuality> wq = waterQualityRepository.findAll();
        List<WeatherData> weather = weatherDataRepository.findAll();
        List<DiseaseRecord> diseases = diseaseRecordRepository.findAll();
        DataProcessingResult result = dispatcher.executeProcessing(wq, weather, diseases);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/spark/history")
    public ResponseEntity<List<SparkJobHistory>> getSparkJobHistory() {
        return ResponseEntity.ok(sparkJobHistoryRepository.findByOrderByExecutionTimestampDesc());
    }

    @GetMapping("/spark/report")
    public ResponseEntity<?> getSparkExecutionReport() {
        try {
            File reportFile = FilePathResolver.resolve("data/processed/spark_execution_report.json");
            if (reportFile != null && reportFile.exists()) {
                String content = Files.readString(reportFile.toPath());
                return ResponseEntity.ok().header("Content-Type", "application/json").body(content);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.notFound().build();
    }
}
