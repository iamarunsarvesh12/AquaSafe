package com.aquasafe.repository;

import com.aquasafe.entity.WaterQuality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterQualityRepository extends JpaRepository<WaterQuality, Long> {

    List<WaterQuality> findByDistrictOrderByDateDesc(String district);

    List<WaterQuality> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    Page<WaterQuality> findByDistrictContainingIgnoreCase(String district, Pageable pageable);

    Optional<WaterQuality> findTopByDistrictOrderByDateDesc(String district);

    @Query("SELECT DISTINCT w.district FROM WaterQuality w ORDER BY w.district ASC")
    List<String> findDistinctDistricts();

    @Query("SELECT AVG(w.ph), AVG(w.turbidity), AVG(w.tds), AVG(w.dissolvedOxygen), AVG(w.waterTemperature) FROM WaterQuality w")
    List<Object[]> getOverallAverages();

    @Query("SELECT w.district, AVG(w.ph), AVG(w.turbidity), AVG(w.tds), AVG(w.dissolvedOxygen), AVG(w.waterTemperature) " +
           "FROM WaterQuality w GROUP BY w.district ORDER BY w.district ASC")
    List<Object[]> getDistrictAverages();
}
