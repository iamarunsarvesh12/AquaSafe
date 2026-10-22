package com.aquasafe.service;

import com.aquasafe.dto.PredictionRequestDTO;
import com.aquasafe.dto.PredictionResponseDTO;
import com.aquasafe.entity.PredictionResult;
import com.aquasafe.ml.WekaModelService;
import com.aquasafe.repository.PredictionResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final WekaModelService mlService;
    private final PredictionResultRepository predictionRepository;
    private final AlertService alertService;

    public PredictionService(
            WekaModelService mlService,
            PredictionResultRepository predictionRepository,
            AlertService alertService) {
        this.mlService = mlService;
        this.predictionRepository = predictionRepository;
        this.alertService = alertService;
    }

    @Transactional
    public PredictionResponseDTO predictRisk(PredictionRequestDTO req) throws Exception {
        // Execute Weka Random Forest ML prediction
        PredictionResponseDTO response = mlService.predict(req);

        // Persist prediction snapshot into database
        PredictionResult entity = new PredictionResult(
            req.getDistrict(),
            response.getRiskLevel(),
            response.getProbability(),
            response.getModelVersion()
        );
        entity.setPh(req.getPh());
        entity.setTurbidity(req.getTurbidity());
        entity.setTds(req.getTds());
        entity.setDissolvedOxygen(req.getDissolvedOxygen());
        entity.setTemperature(req.getTemperature());
        entity.setRainfall(req.getRainfall());
        entity.setHumidity(req.getHumidity());
        entity.setPreviousDiseaseCases(req.getPreviousDiseaseCases());
        entity.setPopulationDensity(req.getPopulationDensity());
        entity.setSanitationCoverage(req.getSanitationCoverage());

        predictionRepository.save(entity);

        // If predicted risk is HIGH, generate an immediate alert
        if ("HIGH".equalsIgnoreCase(response.getRiskLevel())) {
            String alertMsg = "HIGH-RISK CONDITION DETECTED: Estimated high risk condition in " +
                    req.getDistrict() + " (Calculated Probability: " +
                    String.format("%.1f%%", response.getProbability() * 100) +
                    "). Recommendation: Verify regional water treatment and increase epidemiological surveillance.";
            alertService.createAlert(req.getDistrict(), "HIGH", response.getProbability(), alertMsg);
            log.warn("High-risk alert generated for district: {}", req.getDistrict());
        }

        return response;
    }

    public List<PredictionResult> getRecentPredictions() {
        return predictionRepository.findTop10ByOrderByPredictionDateDesc();
    }

    public Page<PredictionResult> getAllPredictions(int page, int size) {
        return predictionRepository.findAllByOrderByPredictionDateDesc(PageRequest.of(page, size));
    }
}
