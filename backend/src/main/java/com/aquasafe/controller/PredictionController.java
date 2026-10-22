package com.aquasafe.controller;

import com.aquasafe.dto.PredictionRequestDTO;
import com.aquasafe.dto.PredictionResponseDTO;
import com.aquasafe.entity.PredictionResult;
import com.aquasafe.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponseDTO> predictRisk(@Valid @RequestBody PredictionRequestDTO request) throws Exception {
        PredictionResponseDTO response = predictionService.predictRisk(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PredictionResult>> getRecentPredictions() {
        return ResponseEntity.ok(predictionService.getRecentPredictions());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PredictionResult>> getAllPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(predictionService.getAllPredictions(page, size));
    }
}
