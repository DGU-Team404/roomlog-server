package com.roomlog.analysis.repository;

import com.roomlog.analysis.domain.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByRoomId(Long roomId);

    Optional<Analysis> findTopByRoomIdOrderByCreatedAtDesc(Long roomId);
}
