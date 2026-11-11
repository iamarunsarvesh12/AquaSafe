package com.aquasafe.repository;

import com.aquasafe.entity.ModelTrainingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelTrainingHistoryRepository extends JpaRepository<ModelTrainingHistory, Long> {
    List<ModelTrainingHistory> findByOrderByTrainingTimestampDesc();
}

