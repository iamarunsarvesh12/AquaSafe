package com.aquasafe.controller;

import com.aquasafe.dto.PreprocessingStatsDTO;
import com.aquasafe.service.DataPreprocessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preprocessing")
public class PreprocessingController {

    private final DataPreprocessingService preprocessingService;

    public PreprocessingController(DataPreprocessingService preprocessingService) {
        this.preprocessingService = preprocessingService;
    }

    @PostMapping("/run")
    public ResponseEntity<PreprocessingStatsDTO> runPreprocessing() {
        PreprocessingStatsDTO stats = preprocessingService.cleanAndIntegrateData();
        return ResponseEntity.ok(stats);
    }
}
