package com.aquasafe.repository;

import com.aquasafe.entity.DatasetSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetSourceRepository extends JpaRepository<DatasetSource, Long> {
    List<DatasetSource> findByDatasetCategory(String datasetCategory);
}

