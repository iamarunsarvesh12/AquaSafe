package com.aquasafe.repository;

import com.aquasafe.entity.PopulationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PopulationRecordRepository extends JpaRepository<PopulationRecord, Long> {
    List<PopulationRecord> findByDistrictNameIgnoreCase(String districtName);
    Optional<PopulationRecord> findByDistrictNameIgnoreCaseAndYear(String districtName, Integer year);
}

