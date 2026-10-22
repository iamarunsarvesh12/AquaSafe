package com.aquasafe.controller;

import com.aquasafe.entity.WaterQuality;
import com.aquasafe.service.WaterQualityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/water-quality")
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
