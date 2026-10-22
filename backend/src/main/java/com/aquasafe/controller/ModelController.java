package com.aquasafe.controller;

import com.aquasafe.dto.ModelEvaluationDTO;
import com.aquasafe.ml.WekaModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
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
}
