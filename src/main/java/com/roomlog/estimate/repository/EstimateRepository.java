package com.roomlog.estimate.repository;

import com.roomlog.estimate.domain.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {

    List<Estimate> findByUserId(Long userId);

    List<Estimate> findByRoomId(Long roomId);

    Optional<Estimate> findByIdAndUserId(Long id, Long userId);
}