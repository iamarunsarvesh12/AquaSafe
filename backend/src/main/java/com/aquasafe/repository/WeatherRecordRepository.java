package com.aquasafe.repository;

import com.aquasafe.entity.WeatherRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
    Page<WeatherRecord> findByDistrictNameIgnoreCase(String districtName, Pageable pageable);
    List<WeatherRecord> findByDistrictNameIgnoreCaseOrderByObservationDateDesc(String districtName);
    List<WeatherRecord> findByObservationDateBetween(LocalDate start, LocalDate end);
    Optional<WeatherRecord> findTopByDistrictNameIgnoreCaseAndObservationDate(String districtName, LocalDate observationDate);
    Optional<WeatherRecord> findTopByDistrictNameIgnoreCaseOrderByObservationDateDesc(String districtName);
}
