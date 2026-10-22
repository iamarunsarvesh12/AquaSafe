package com.aquasafe.repository;

import com.aquasafe.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findTop10ByOrderByCreatedAtDesc();

    List<Alert> findByStatusOrderByCreatedAtDesc(String status);

    Page<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(String status);
}
