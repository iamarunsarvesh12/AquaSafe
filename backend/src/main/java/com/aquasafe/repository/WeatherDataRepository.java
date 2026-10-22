package com.aquasafe.repository;

import com.aquasafe.entity.WeatherData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    List<WeatherData> findByDistrictOrderByDateDesc(String district);

    List<WeatherData> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    Page<WeatherData> findByDistrictContainingIgnoreCase(String district, Pageable pageable);

    Optional<WeatherData> findTopByDistrictOrderByDateDesc(String district);

    @Query("SELECT DISTINCT w.district FROM WeatherData w ORDER BY w.district ASC")
    List<String> findDistinctDistricts();

    @Query("SELECT AVG(w.temperature), AVG(w.rainfall), AVG(w.humidity) FROM WeatherData w")
    List<Object[]> getOverallAverages();

    @Query("SELECT w.district, AVG(w.temperature), AVG(w.rainfall), AVG(w.humidity) " +
           "FROM WeatherData w GROUP BY w.district ORDER BY w.district ASC")
    List<Object[]> getDistrictAverages();
}
