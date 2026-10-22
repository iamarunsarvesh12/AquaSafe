package com.aquasafe.service;

import com.aquasafe.entity.WaterQuality;
import com.aquasafe.repository.WaterQualityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
