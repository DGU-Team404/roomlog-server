package com.roomlog.repair.repository;

import com.roomlog.repair.domain.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {

    List<Repair> findByRoomId(Long roomId);
}
