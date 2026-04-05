package com.roomlog.repair.repository;

import com.roomlog.repair.domain.RepairDefect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairDefectRepository extends JpaRepository<RepairDefect, Long> {

    List<RepairDefect> findByRepairId(Long repairId);
}
