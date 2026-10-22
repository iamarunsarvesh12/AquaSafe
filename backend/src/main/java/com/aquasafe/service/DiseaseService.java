package com.aquasafe.service;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.repository.DiseaseRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiseaseService {

    private final DiseaseRecordRepository diseaseRepository;

    public DiseaseService(DiseaseRecordRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    public Page<DiseaseRecord> getRecords(String district, int page, int size) {
        if (district != null && !district.trim().isEmpty()) {
            return diseaseRepository.findByDistrictContainingIgnoreCase(district, PageRequest.of(page, size));
        }
        return diseaseRepository.findAll(PageRequest.of(page, size));
    }

    public List<DiseaseRecord> getRecordsByDistrict(String district) {
        return diseaseRepository.findByDistrictOrderByDateDesc(district);
    }

    public List<DiseaseRecord> getRecordsByDateRange(LocalDate start, LocalDate end) {
        return diseaseRepository.findByDateBetweenOrderByDateAsc(start, end);
    }

    public List<Object[]> getTotalDiseaseCases() {
        return diseaseRepository.getTotalDiseaseCases();
    }

    public List<Object[]> getDistrictWiseTotals() {
        return diseaseRepository.getDistrictWiseTotals();
    }

    public List<Object[]> getTimelineTotals() {
        return diseaseRepository.getTimelineTotals();
    }
}
