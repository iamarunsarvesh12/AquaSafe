package com.aquasafe.repository;

import com.aquasafe.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    List<AlertHistory> findByAlertIdOrderByActionTimestampDesc(Long alertId);
    List<AlertHistory> findByDistrictIgnoreCaseOrderByActionTimestampDesc(String district);
}

