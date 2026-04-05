package com.roomlog.defect.repository;

import com.roomlog.defect.domain.Defect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefectRepository extends JpaRepository<Defect, Long> {

    List<Defect> findByAnalysisId(Long analysisId);
}
