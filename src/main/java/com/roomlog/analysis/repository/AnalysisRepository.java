package com.roomlog.analysis.repository;

import com.roomlog.analysis.domain.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    @Query("SELECT a FROM Analysis a WHERE a.inRoomId = :roomId OR a.outRoomId = :roomId")
    List<Analysis> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT a FROM Analysis a WHERE (a.inRoomId = :roomId OR a.outRoomId = :roomId) ORDER BY a.createdAt DESC LIMIT 1")
    Optional<Analysis> findLatestByRoomId(@Param("roomId") Long roomId);
}
