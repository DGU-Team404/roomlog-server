package com.roomlog.analysis.repository;

import com.roomlog.analysis.domain.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByRoomId(Long roomId);

    List<Analysis> findByRoomIdIn(List<Long> roomIds);

    Optional<Analysis> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
}
