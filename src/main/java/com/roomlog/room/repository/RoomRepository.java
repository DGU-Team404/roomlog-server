package com.roomlog.room.repository;

import com.roomlog.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByUserId(Long userId);

    Optional<Room> findByIdAndUserId(Long id, Long userId);
}
