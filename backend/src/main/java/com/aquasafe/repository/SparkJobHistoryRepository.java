package com.aquasafe.repository;

import com.aquasafe.entity.SparkJobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparkJobHistoryRepository extends JpaRepository<SparkJobHistory, Long> {
    List<SparkJobHistory> findByOrderByExecutionTimestampDesc();
}

