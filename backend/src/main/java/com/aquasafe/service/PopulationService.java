package com.aquasafe.service;

import com.aquasafe.entity.PopulationData;
import com.aquasafe.repository.PopulationDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PopulationService {

    private final PopulationDataRepository populationRepository;

    public PopulationService(PopulationDataRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    public List<PopulationData> getAllDistricts() {
        return populationRepository.findAll();
    }

    public Optional<PopulationData> getByDistrict(String district) {
        return populationRepository.findByDistrictIgnoreCase(district);
    }
}
