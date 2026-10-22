package com.aquasafe.repository;

import com.aquasafe.entity.PredictionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    List<PredictionResult> findTop10ByOrderByPredictionDateDesc();

    List<PredictionResult> findByDistrictOrderByPredictionDateDesc(String district);

    Page<PredictionResult> findAllByOrderByPredictionDateDesc(Pageable pageable);

    @Query("SELECT p.riskLevel, COUNT(p) FROM PredictionResult p GROUP BY p.riskLevel")
    List<Object[]> getRiskDistribution();

    @Query("SELECT p.district, p.riskLevel, p.probability, p.predictionDate FROM PredictionResult p " +
           "WHERE p.id IN (SELECT MAX(p2.id) FROM PredictionResult p2 GROUP BY p2.district)")
    List<Object[]> getLatestPredictionPerDistrict();

    long countByRiskLevel(String riskLevel);
}
