package com.aquasafe.controller;

import com.aquasafe.config.FilePathResolver;
import com.aquasafe.dto.ModelEvaluationDTO;
import com.aquasafe.ml.WekaModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/model")
@CrossOrigin(originPatterns = "*")
public class ModelController {

    private final WekaModelService mlService;

    public ModelController(WekaModelService mlService) {
        this.mlService = mlService;
    }

    @PostMapping("/train")
    public ResponseEntity<ModelEvaluationDTO> trainModel() throws Exception {
        ModelEvaluationDTO eval = mlService.trainModel();
        return ResponseEntity.ok(eval);
    }

    @GetMapping("/evaluation")
    public ResponseEntity<ModelEvaluationDTO> getEvaluation() {
        ModelEvaluationDTO eval = mlService.getLatestEvaluation();
        if (eval == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eval);
    }

    @GetMapping("/comparison")
    public ResponseEntity<?> getModelComparison() {
        try {
            File file = FilePathResolver.resolve("ml/models/model_evaluation_metrics.json");
            if (file != null && file.exists()) {
                String json = Files.readString(file.toPath());
                return ResponseEntity.ok().header("Content-Type", "application/json").body(json);
            }
        } catch (Exception ignored) {}

        ModelEvaluationDTO eval = mlService.getLatestEvaluation();
        return ResponseEntity.ok(eval);
    }
}
