package com.aquasafe.repository;

import com.aquasafe.entity.WaterQualityRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaterQualityRecordRepository extends JpaRepository<WaterQualityRecord, Long> {
    Page<WaterQualityRecord> findByDistrictNameIgnoreCase(String districtName, Pageable pageable);
    List<WaterQualityRecord> findByDistrictNameIgnoreCaseOrderByObservationDateDesc(String districtName);
    List<WaterQualityRecord> findByObservationDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT AVG(w.ph), AVG(w.turbidity), AVG(w.tds), AVG(w.dissolvedOxygen) FROM WaterQualityRecord w WHERE LOWER(w.districtName) = LOWER(?1)")
    Object[] findDistrictAverages(String districtName);
}

