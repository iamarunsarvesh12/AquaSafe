package com.aquasafe.controller;

import com.aquasafe.entity.WaterQuality;
import com.aquasafe.service.WaterQualityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water-quality")
@CrossOrigin(originPatterns = "*")
public class WaterQualityController {

    private final WaterQualityService waterQualityService;

    public WaterQualityController(WaterQualityService waterQualityService) {
        this.waterQualityService = waterQualityService;
    }

    @GetMapping
    public ResponseEntity<Page<WaterQuality>> getWaterQualityRecords(
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(waterQualityService.getRecords(district, page, size));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getComprehensiveAnalytics() {
        return ResponseEntity.ok(waterQualityService.getComprehensiveAnalytics());
    }

    @GetMapping("/violations")
    public ResponseEntity<List<Map<String, Object>>> getStandardsViolations() {
        return ResponseEntity.ok(waterQualityService.getStandardsViolations());
    }

    @GetMapping("/averages")
    public ResponseEntity<List<Object[]>> getDistrictAverages() {
        return ResponseEntity.ok(waterQualityService.getDistrictAverages());
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestByDistrict(@RequestParam String district) {
        return waterQualityService.getLatestByDistrict(district)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
