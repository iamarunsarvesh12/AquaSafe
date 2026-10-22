package com.aquasafe.repository;

import com.aquasafe.entity.PopulationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PopulationDataRepository extends JpaRepository<PopulationData, Long> {
    Optional<PopulationData> findByDistrictIgnoreCase(String district);
    boolean existsByDistrictIgnoreCase(String district);
}
