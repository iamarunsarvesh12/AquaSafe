package com.aquasafe.repository;

import com.aquasafe.entity.ProcessedAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessedAnalyticsRepository extends JpaRepository<ProcessedAnalytics, Long> {
    List<ProcessedAnalytics> findByDistrictNameIgnoreCaseOrderByAnalysisDateDesc(String districtName);
    Optional<ProcessedAnalytics> findFirstByDistrictNameIgnoreCaseOrderByAnalysisDateDesc(String districtName);
    List<ProcessedAnalytics> findByAnalysisDateBetween(LocalDate start, LocalDate end);
}

