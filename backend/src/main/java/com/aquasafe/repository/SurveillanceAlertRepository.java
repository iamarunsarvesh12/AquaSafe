package com.aquasafe.repository;

import com.aquasafe.entity.SurveillanceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveillanceAlertRepository extends JpaRepository<SurveillanceAlert, Long> {
    List<SurveillanceAlert> findByStatusOrderByCreatedAtDesc(String status);
    List<SurveillanceAlert> findByDistrictIgnoreCaseOrderByCreatedAtDesc(String district);
    List<SurveillanceAlert> findByOrderByCreatedAtDesc();
}

