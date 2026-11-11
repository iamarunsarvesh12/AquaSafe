package com.aquasafe.repository;

import com.aquasafe.entity.DiseaseForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseForecastRepository extends JpaRepository<DiseaseForecast, Long> {
    List<DiseaseForecast> findByDistrictIgnoreCaseOrderByForecastDateAsc(String district);
    List<DiseaseForecast> findByDistrictIgnoreCaseAndDiseaseNameIgnoreCaseOrderByForecastDateAsc(String district, String diseaseName);
}

