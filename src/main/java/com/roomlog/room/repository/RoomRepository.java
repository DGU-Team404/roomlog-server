package com.roomlog.room.repository;

import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // H05: 특정 집에 속한 방 목록
    List<Room> findByHouseId(Long houseId);

    Optional<Room> findByIdAndHouseId(Long id, Long houseId);

    // V02: 입주/퇴거 스캔 타입 기준 방 목록 조회
    @Query("SELECT r FROM Room r JOIN Scan s ON s.roomId = r.id WHERE r.houseId = :houseId AND s.scanType = :scanType")
    List<Room> findByHouseIdAndScanType(@Param("houseId") Long houseId, @Param("scanType") Scan.ScanType scanType);
}
