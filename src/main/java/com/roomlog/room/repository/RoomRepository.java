package com.roomlog.room.repository;

import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByUserId(Long userId);

    Optional<Room> findByIdAndUserId(Long id, Long userId);

    // V02: 입주/퇴거 스캔 타입 기준 방 목록 조회
    @Query("SELECT r FROM Room r JOIN Scan s ON s.roomId = r.id WHERE r.userId = :userId AND s.scanType = :scanType")
    List<Room> findByUserIdAndScanType(@Param("userId") Long userId, @Param("scanType") Scan.ScanType scanType);

    // H06: 대표 방 재설정 - 가장 최근 스캔이 있는 방
    @Query("SELECT r FROM Room r JOIN Scan s ON s.roomId = r.id WHERE r.userId = :userId ORDER BY s.createdAt DESC LIMIT 1")
    Optional<Room> findTopByUserIdOrderByLatestScanDesc(@Param("userId") Long userId);
}
