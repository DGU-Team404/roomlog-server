package com.roomlog.room.repository;

import com.roomlog.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // H05: 특정 집에 속한 방 목록
    List<Room> findByHouseId(Long houseId);

    Optional<Room> findByIdAndHouseId(Long id, Long houseId);

}
