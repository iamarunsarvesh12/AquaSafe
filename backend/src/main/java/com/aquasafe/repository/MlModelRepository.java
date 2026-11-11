package com.aquasafe.repository;

import com.aquasafe.entity.MlModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MlModelRepository extends JpaRepository<MlModelEntity, Long> {
    Optional<MlModelEntity> findByModelVersion(String modelVersion);
    List<MlModelEntity> findByActiveTrue();
    List<MlModelEntity> findByModelType(String modelType);
}

