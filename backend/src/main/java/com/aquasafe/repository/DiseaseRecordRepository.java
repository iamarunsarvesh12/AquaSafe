package com.aquasafe.repository;

import com.aquasafe.entity.DiseaseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiseaseRecordRepository extends JpaRepository<DiseaseRecord, Long> {

    List<DiseaseRecord> findByDistrictOrderByDateDesc(String district);

    List<DiseaseRecord> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    Page<DiseaseRecord> findByDistrictContainingIgnoreCase(String district, Pageable pageable);

    @Query("SELECT DISTINCT d.district FROM DiseaseRecord d ORDER BY d.district ASC")
    List<String> findDistinctDistricts();

    @Query("SELECT SUM(d.choleraCases), SUM(d.typhoidCases), SUM(d.diarrheaCases), SUM(d.hepatitisCases) FROM DiseaseRecord d")
    List<Object[]> getTotalDiseaseCases();

    @Query("SELECT d.district, SUM(d.choleraCases), SUM(d.typhoidCases), SUM(d.diarrheaCases), SUM(d.hepatitisCases) " +
           "FROM DiseaseRecord d GROUP BY d.district ORDER BY d.district ASC")
    List<Object[]> getDistrictWiseTotals();

    @Query("SELECT d.date, SUM(d.choleraCases), SUM(d.typhoidCases), SUM(d.diarrheaCases), SUM(d.hepatitisCases) " +
           "FROM DiseaseRecord d GROUP BY d.date ORDER BY d.date ASC")
    List<Object[]> getTimelineTotals();
}
