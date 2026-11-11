package com.aquasafe.controller;

import com.aquasafe.entity.District;
import com.aquasafe.repository.DistrictRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/districts")
@CrossOrigin(originPatterns = "*")
public class DistrictController {

    private final DistrictRepository districtRepository;

    public DistrictController(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    @GetMapping
    public ResponseEntity<List<District>> getAllDistricts() {
        return ResponseEntity.ok(districtRepository.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getDistrictByName(@PathVariable String name) {
        Optional<District> d = districtRepository.findByDistrictNameIgnoreCase(name);
        return d.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDistrictsSummary() {
        List<District> list = districtRepository.findAll();
        long totalPop = list.stream().mapToLong(District::getPopulation2025).sum();
        double avgDensity = list.stream().mapToInt(District::getPopulationDensity).average().orElse(0.0);
        double avgSanitation = list.stream().mapToDouble(District::getSanitationCoverage).average().orElse(0.0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalDistricts", list.size());
        summary.put("state", "Tamil Nadu");
        summary.put("totalPopulation2025", totalPop);
        summary.put("averagePopulationDensity", Math.round(avgDensity));
        summary.put("averageSanitationCoverage", Math.round(avgSanitation * 10.0) / 10.0);
        summary.put("districts", list);

        return ResponseEntity.ok(summary);
    }
}

