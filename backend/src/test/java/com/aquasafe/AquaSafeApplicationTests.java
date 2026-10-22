package com.aquasafe;

import com.aquasafe.dto.PredictionRequestDTO;
import com.aquasafe.dto.PredictionResponseDTO;
import com.aquasafe.dto.PreprocessingStatsDTO;
import com.aquasafe.service.DataPreprocessingService;
import com.aquasafe.service.PredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AquaSafeApplicationTests {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private DataPreprocessingService preprocessingService;

    @Test
    void contextLoads() {
        assertNotNull(predictionService, "PredictionService should be injected");
        assertNotNull(preprocessingService, "DataPreprocessingService should be injected");
    }

    @Test
    void testModelPrediction() throws Exception {
        PredictionRequestDTO req = new PredictionRequestDTO();
        req.setDistrict("Chennai");
        req.setPh(7.2);
        req.setTurbidity(3.5);
        req.setTds(320.0);
        req.setDissolvedOxygen(6.8);
        req.setTemperature(29.0);
        req.setRainfall(15.0);
        req.setHumidity(65.0);
        req.setPreviousDiseaseCases(5);
        req.setPopulationDensity(26900);
        req.setSanitationCoverage(92.5);

        PredictionResponseDTO response = predictionService.predictRisk(req);
        assertNotNull(response);
        assertEquals("Chennai", response.getDistrict());
        assertNotNull(response.getRiskLevel(), "Risk level should not be null");
        assertTrue(response.getProbability() >= 0.0 && response.getProbability() <= 1.0, "Probability must be between 0 and 1");
        assertNotNull(response.getClassProbabilities());
        assertNotNull(response.getDisclaimer(), "Public health disclaimer must be present");
    }

    @Test
    void testPreprocessingAndLabeling() {
        String highRisk = preprocessingService.determineRiskLabel(6.0, 9.5, 4.0, 85.0, 30, 15000, 68.0);
        assertEquals("HIGH", highRisk, "Extreme indicators should evaluate to HIGH risk");

        String lowRisk = preprocessingService.determineRiskLabel(7.4, 2.0, 7.5, 5.0, 2, 400, 92.0);
        assertEquals("LOW", lowRisk, "Safe indicators should evaluate to LOW risk");

        PreprocessingStatsDTO stats = preprocessingService.cleanAndIntegrateData();
        assertNotNull(stats);
        assertEquals("SUCCESS", stats.getStatus());
        assertTrue(stats.getRecordsAfterCleaning() > 0);
    }
}
