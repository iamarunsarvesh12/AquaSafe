package com.aquasafe.repository;

import com.aquasafe.entity.DatasetImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetImportRepository extends JpaRepository<DatasetImport, Long> {
    List<DatasetImport> findTop10ByOrderByImportTimeDesc();
    Page<DatasetImport> findAllByOrderByImportTimeDesc(Pageable pageable);
}
