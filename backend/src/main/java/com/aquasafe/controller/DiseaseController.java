package com.aquasafe.controller;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.service.DiseaseService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @GetMapping
    public ResponseEntity<Page<DiseaseRecord>> getDiseaseRecords(
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(diseaseService.getRecords(district, page, size));
    }

    @GetMapping("/totals")
    public ResponseEntity<List<Object[]>> getTotalDiseaseCases() {
        return ResponseEntity.ok(diseaseService.getTotalDiseaseCases());
    }

    @GetMapping("/district-totals")
    public ResponseEntity<List<Object[]>> getDistrictWiseTotals() {
        return ResponseEntity.ok(diseaseService.getDistrictWiseTotals());
    }
}
