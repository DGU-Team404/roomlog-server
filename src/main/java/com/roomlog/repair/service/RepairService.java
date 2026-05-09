package com.roomlog.repair.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.repair.domain.Repair;
import com.roomlog.repair.domain.RepairDefect;
import com.roomlog.repair.dto.GetRepairListResponse;
import com.roomlog.repair.dto.RepairListItemResponse;
import com.roomlog.repair.repository.RepairDefectRepository;
import com.roomlog.repair.repository.RepairRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final RepairRepository repairRepository;
    private final RepairDefectRepository repairDefectRepository;

    @Transactional(readOnly = true)
    public GetRepairListResponse getRepairList(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        List<Repair> repairs = repairRepository.findByRoomId(roomId);

        List<Long> repairIds = repairs.stream().map(Repair::getId).toList();
        Map<Long, List<RepairDefect>> defectsByRepairId = repairDefectRepository
                .findByRepairIdIn(repairIds).stream()
                .collect(Collectors.groupingBy(RepairDefect::getRepairId));

        List<RepairListItemResponse> items = repairs.stream()
                .map(r -> RepairListItemResponse.of(r, defectsByRepairId.getOrDefault(r.getId(), List.of())))
                .toList();

        return GetRepairListResponse.of(roomId, items);
    }
}