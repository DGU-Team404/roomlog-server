package com.roomlog.estimate.repository;

import com.roomlog.estimate.domain.EstimateDefect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateDefectRepository extends JpaRepository<EstimateDefect, Long> {

    List<EstimateDefect> findByEstimateId(Long estimateId);
}