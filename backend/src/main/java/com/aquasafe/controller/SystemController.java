package com.aquasafe.controller;

import com.aquasafe.dto.SystemHealthDTO;
import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;
import com.aquasafe.repository.DiseaseRecordRepository;
import com.aquasafe.repository.WaterQualityRepository;
import com.aquasafe.repository.WeatherDataRepository;
import com.aquasafe.service.DashboardService;
import com.aquasafe.spark.DataProcessingDispatcher;
import com.aquasafe.spark.DataProcessingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final DashboardService dashboardService;
    private final DataProcessingDispatcher dispatcher;
    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;

    public SystemController(
            DashboardService dashboardService,
            DataProcessingDispatcher dispatcher,
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository) {
        this.dashboardService = dashboardService;
        this.dispatcher = dispatcher;
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
    }

    @GetMapping("/health")
    public ResponseEntity<SystemHealthDTO> getSystemHealth() {
        return ResponseEntity.ok(dashboardService.getSystemHealth());
    }

    @PostMapping("/spark/process")
    public ResponseEntity<DataProcessingResult> triggerBigDataProcessing() {
        List<WaterQuality> wq = waterQualityRepository.findAll();
        List<WeatherData> weather = weatherDataRepository.findAll();
        List<DiseaseRecord> diseases = diseaseRecordRepository.findAll();

        DataProcessingResult result = dispatcher.executeProcessing(wq, weather, diseases);
        return ResponseEntity.ok(result);
    }
}
