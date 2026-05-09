package com.roomlog.repair.service;

import com.roomlog.estimate.domain.Estimate;
import com.roomlog.estimate.domain.EstimateDefect;
import com.roomlog.estimate.repository.EstimateDefectRepository;
import com.roomlog.estimate.repository.EstimateRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.repair.domain.Repair;
import com.roomlog.repair.domain.RepairDefect;
import com.roomlog.repair.dto.CreateRepairRequest;
import com.roomlog.repair.dto.CreateRepairResponse;
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
    private final EstimateRepository estimateRepository;
    private final EstimateDefectRepository estimateDefectRepository;
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

    @Transactional
    public CreateRepairResponse registerRepair(Long userId, Long estimateId, CreateRepairRequest request) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTIMATE_002));

        Room room = roomRepository.findById(estimate.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        Repair repair = Repair.builder()
                .roomId(estimate.getRoomId())
                .estimateId(estimateId)
                .providerName(estimate.getProviderName())
                .repairCost(request.getRepairCost())
                .note(request.getNote())
                .build();
        repair.complete();
        repairRepository.save(repair);

        List<EstimateDefect> estimateDefects = estimateDefectRepository.findByEstimateId(estimateId);
        List<RepairDefect> repairDefects = estimateDefects.stream()
                .map(ed -> RepairDefect.builder()
                        .repairId(repair.getId())
                        .defectId(ed.getDefectId())
                        .build())
                .toList();
        repairDefectRepository.saveAll(repairDefects);

        estimate.updateStatus(Estimate.Status.COMPLETED);

        return CreateRepairResponse.of(repair);
    }
}
