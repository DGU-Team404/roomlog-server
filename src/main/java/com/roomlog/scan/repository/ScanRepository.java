package com.roomlog.scan.repository;

import com.roomlog.scan.domain.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ScanRepository extends JpaRepository<Scan, Long> {

    List<Scan> findByRoomId(Long roomId);

    List<Scan> findByRoomIdIn(List<Long> roomIds);

    Optional<Scan> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
}
