package com.aquasafe.controller;

import com.aquasafe.entity.PredictionResult;
import com.aquasafe.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;
    private final PredictionResultRepository predictionRepository;
    private final PopulationDataRepository populationRepository;

    public ReportController(
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository,
            PredictionResultRepository predictionRepository,
            PopulationDataRepository populationRepository) {
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
        this.predictionRepository = predictionRepository;
        this.populationRepository = populationRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("title", "AquaSafe: Water-Borne Disease Risk Intelligence Report");
        report.put("generatedAt", LocalDateTime.now());
        report.put("periodStart", startDate);
        report.put("periodEnd", endDate);
        report.put("filterDistrict", district != null ? district : "All Districts (Tamil Nadu)");

        // 1. Risk Summary
        List<PredictionResult> predictions;
        if (district != null && !district.trim().isEmpty() && !district.equalsIgnoreCase("All")) {
            predictions = predictionRepository.findByDistrictOrderByPredictionDateDesc(district);
        } else {
            predictions = predictionRepository.findTop10ByOrderByPredictionDateDesc();
        }
        report.put("predictionHistory", predictions);

        long highCount = predictions.stream().filter(p -> "HIGH".equalsIgnoreCase(p.getRiskLevel())).count();
        long medCount = predictions.stream().filter(p -> "MEDIUM".equalsIgnoreCase(p.getRiskLevel())).count();
        long lowCount = predictions.stream().filter(p -> "LOW".equalsIgnoreCase(p.getRiskLevel())).count();

        Map<String, Object> riskSummary = new LinkedHashMap<>();
        riskSummary.put("highRiskCount", highCount);
        riskSummary.put("mediumRiskCount", medCount);
        riskSummary.put("lowRiskCount", lowCount);
        riskSummary.put("totalEvaluated", predictions.size());
        report.put("riskSummary", riskSummary);

        // 2. Disease Summary
        List<Object[]> diseaseTotals = diseaseRecordRepository.getTotalDiseaseCases();
        Map<String, Object> diseaseSummary = new LinkedHashMap<>();
        if (!diseaseTotals.isEmpty() && diseaseTotals.get(0)[0] != null) {
            Object[] dt = diseaseTotals.get(0);
            diseaseSummary.put("choleraTotal", dt[0]);
            diseaseSummary.put("typhoidTotal", dt[1]);
            diseaseSummary.put("diarrheaTotal", dt[2]);
            diseaseSummary.put("hepatitisTotal", dt[3]);
        }
        report.put("diseaseSummary", diseaseSummary);

        // 3. Water Quality Summary
        List<Object[]> wqAvgs = waterQualityRepository.getOverallAverages();
        Map<String, Object> wqSummary = new LinkedHashMap<>();
        if (!wqAvgs.isEmpty() && wqAvgs.get(0)[0] != null) {
            Object[] wq = wqAvgs.get(0);
            wqSummary.put("avgPh", round(((Number) wq[0]).doubleValue()));
            wqSummary.put("avgTurbidity", round(((Number) wq[1]).doubleValue()));
            wqSummary.put("avgTds", round(((Number) wq[2]).doubleValue()));
            wqSummary.put("avgDissolvedOxygen", round(((Number) wq[3]).doubleValue()));
            wqSummary.put("avgWaterTemp", round(((Number) wq[4]).doubleValue()));
        }
        report.put("waterQualitySummary", wqSummary);

        // 4. Weather Summary
        List<Object[]> weatherAvgs = weatherDataRepository.getOverallAverages();
        Map<String, Object> weatherSummary = new LinkedHashMap<>();
        if (!weatherAvgs.isEmpty() && weatherAvgs.get(0)[0] != null) {
            Object[] wt = weatherAvgs.get(0);
            weatherSummary.put("avgTemperature", round(((Number) wt[0]).doubleValue()));
            weatherSummary.put("avgRainfall", round(((Number) wt[1]).doubleValue()));
            weatherSummary.put("avgHumidity", round(((Number) wt[2]).doubleValue()));
        }
        report.put("weatherSummary", weatherSummary);

        // 5. Key Findings & Recommendations
        List<String> keyFindings = new ArrayList<>();
        if (highCount > 0) {
            keyFindings.add("Elevated water-borne risk conditions identified in monitored districts. Increased surveillance recommended.");
        } else {
            keyFindings.add("All monitored zones are within nominal risk thresholds based on recorded indicators.");
        }
        keyFindings.add("Seasonal precipitation patterns exhibit significant correlation with transient turbidity spikes.");
        keyFindings.add("Continuous monitoring of dissolved oxygen and microbiological filtration efficacy is recommended.");
        report.put("keyFindings", keyFindings);

        report.put("disclaimer", "Prediction indicates estimated risk based on available project data. It is not a medical diagnosis or confirmation of an outbreak.");

        return ResponseEntity.ok(report);
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
