package com.aquasafe.service;

import com.aquasafe.entity.WaterQuality;
import com.aquasafe.repository.WaterQualityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaterQualityService {

    private final WaterQualityRepository waterQualityRepository;

    public WaterQualityService(WaterQualityRepository waterQualityRepository) {
        this.waterQualityRepository = waterQualityRepository;
    }

    public Page<WaterQuality> getRecords(String district, int page, int size) {
        if (district != null && !district.trim().isEmpty()) {
            return waterQualityRepository.findByDistrictContainingIgnoreCase(district, PageRequest.of(page, size));
        }
        return waterQualityRepository.findAll(PageRequest.of(page, size));
    }

    public List<WaterQuality> getRecentRecordsByDistrict(String district) {
        return waterQualityRepository.findByDistrictOrderByDateDesc(district);
    }

    public Optional<WaterQuality> getLatestByDistrict(String district) {
        return waterQualityRepository.findTopByDistrictOrderByDateDesc(district);
    }

    public List<Object[]> getDistrictAverages() {
        return waterQualityRepository.getDistrictAverages();
    }

    public List<Object[]> getOverallAverages() {
        return waterQualityRepository.getOverallAverages();
    }

    public Map<String, Object> getComprehensiveAnalytics() {
        List<WaterQuality> all = waterQualityRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        if (all.isEmpty()) {
            result.put("totalRecords", 0);
            return result;
        }

        double avgPh = all.stream().mapToDouble(WaterQuality::getPh).average().orElse(7.0);
        double avgTurbidity = all.stream().mapToDouble(WaterQuality::getTurbidity).average().orElse(3.0);
        double avgTds = all.stream().mapToDouble(WaterQuality::getTds).average().orElse(400.0);
        double avgDo = all.stream().mapToDouble(WaterQuality::getDissolvedOxygen).average().orElse(6.0);
        double avgTemp = all.stream().mapToDouble(WaterQuality::getWaterTemperature).average().orElse(25.0);

        result.put("totalRecords", all.size());
        result.put("averagePh", Math.round(avgPh * 100.0) / 100.0);
        result.put("averageTurbidity", Math.round(avgTurbidity * 100.0) / 100.0);
        result.put("averageTds", Math.round(avgTds * 10.0) / 10.0);
        result.put("averageDissolvedOxygen", Math.round(avgDo * 100.0) / 100.0);
        result.put("averageWaterTemperature", Math.round(avgTemp * 10.0) / 10.0);
        result.put("dataSource", "Central Ground Water Board (CGWB) & Surface Hydrograph Standards");
        result.put("measurementProtocol", "Quarterly & Pre/Post-Monsoon Laboratory Testing (BIS 10500:2012)");
        result.put("stationType", "GROUNDWATER_LAB");
        result.put("liveSensorStatus", "PERIODIC LABORATORY AUDIT (IoT sensors not present in deep aquifers)");
        result.put("dataFreshness", "VERIFIED OFFICIAL OBSERVATION REGISTRY");

        // Group by district
        Map<String, List<WaterQuality>> byDistrict = all.stream()
                .collect(Collectors.groupingBy(WaterQuality::getDistrict));

        List<Map<String, Object>> districtList = new ArrayList<>();
        for (Map.Entry<String, List<WaterQuality>> entry : byDistrict.entrySet()) {
            String dist = entry.getKey();
            List<WaterQuality> distRecords = entry.getValue();

            double dPh = distRecords.stream().mapToDouble(WaterQuality::getPh).average().orElse(7.0);
            double dTurb = distRecords.stream().mapToDouble(WaterQuality::getTurbidity).average().orElse(3.0);
            double dTds = distRecords.stream().mapToDouble(WaterQuality::getTds).average().orElse(400.0);
            double dDo = distRecords.stream().mapToDouble(WaterQuality::getDissolvedOxygen).average().orElse(6.0);

            // Water Quality Index (WQI) Weighted Arithmetic
            double wqi = Math.round((Math.pow(dPh - 7.0, 2) * 12.0 + dTurb * 5.5 + (dTds / 100.0) * 4.2 + (dDo < 5.0 ? (5.0 - dDo) * 15.0 : 0.0)) * 100.0) / 100.0;

            String status = wqi < 40.0 ? "EXCELLENT" : (wqi < 65.0 ? "MODERATE / GOOD" : "POOR / CONTAMINATED");

            Map<String, Object> dm = new LinkedHashMap<>();
            dm.put("district", dist);
            dm.put("avgPh", Math.round(dPh * 100.0) / 100.0);
            dm.put("avgTurbidity", Math.round(dTurb * 100.0) / 100.0);
            dm.put("avgTds", Math.round(dTds * 10.0) / 10.0);
            dm.put("avgDissolvedOxygen", Math.round(dDo * 100.0) / 100.0);
            dm.put("calculatedWqi", wqi);
            dm.put("wqiStatus", status);
            districtList.add(dm);
        }

        districtList.sort((a, b) -> Double.compare((Double) b.get("calculatedWqi"), (Double) a.get("calculatedWqi")));
        result.put("districtAnalytics", districtList);

        return result;
    }

    public List<Map<String, Object>> getStandardsViolations() {
        List<WaterQuality> all = waterQualityRepository.findAll();
        List<Map<String, Object>> violations = new ArrayList<>();

        for (WaterQuality w : all) {
            // Check BIS 10500:2012 Drinking Water Limits
            if (w.getTurbidity() > 5.0) {
                violations.add(createViolation(w, "Turbidity", w.getTurbidity(), "NTU", "5.0 NTU (BIS 10500)", w.getTurbidity() > 8.0 ? "CRITICAL" : "WARNING"));
            }
            if (w.getPh() < 6.5 || w.getPh() > 8.5) {
                violations.add(createViolation(w, "pH", w.getPh(), "pH units", "6.5 - 8.5 (BIS 10500)", "WARNING"));
            }
            if (w.getTds() > 500.0) {
                violations.add(createViolation(w, "TDS", w.getTds(), "mg/L", "500.0 mg/L (BIS 10500)", w.getTds() > 1000.0 ? "CRITICAL" : "MODERATE"));
            }
            if (w.getDissolvedOxygen() < 5.0) {
                violations.add(createViolation(w, "Dissolved Oxygen", w.getDissolvedOxygen(), "mg/L", ">= 5.0 mg/L (CPCB Class C)", "WARNING"));
            }
        }

        violations.sort((a, b) -> ((String) b.get("date")).compareTo((String) a.get("date")));
        return violations.stream().limit(100).collect(Collectors.toList());
    }

    private Map<String, Object> createViolation(WaterQuality w, String param, double val, String unit, String standard, String severity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("district", w.getDistrict());
        map.put("date", w.getDate().toString());
        map.put("parameter", param);
        map.put("observedValue", val);
        map.put("unit", unit);
        map.put("permissibleStandard", standard);
        map.put("severity", severity);
        return map;
    }
}
