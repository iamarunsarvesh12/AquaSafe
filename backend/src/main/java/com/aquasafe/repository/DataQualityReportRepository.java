package com.aquasafe.repository;

import com.aquasafe.entity.DataQualityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataQualityReportRepository extends JpaRepository<DataQualityReport, Long> {
    List<DataQualityReport> findByOrderByCheckDateDesc();
}

