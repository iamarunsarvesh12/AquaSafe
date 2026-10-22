package com.aquasafe.controller;

import com.aquasafe.entity.PopulationData;
import com.aquasafe.service.PopulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/population")
public class PopulationController {

    private final PopulationService populationService;

    public PopulationController(PopulationService populationService) {
        this.populationService = populationService;
    }

    @GetMapping
    public ResponseEntity<List<PopulationData>> getAllDistricts() {
        return ResponseEntity.ok(populationService.getAllDistricts());
    }

    @GetMapping("/{district}")
    public ResponseEntity<PopulationData> getByDistrict(@PathVariable String district) {
        return populationService.getByDistrict(district)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
