package com.roomlog.house.repository;

import com.roomlog.house.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {

    List<House> findByUserId(Long userId);

    Optional<House> findByIdAndUserId(Long id, Long userId);

    // 집 삭제 후 대표 집 자동 재설정용
    Optional<House> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
