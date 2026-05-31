package com.roomlog.room.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.estimate.repository.EstimateRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.repair.repository.RepairRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.dto.CreateRoomRequest;
import com.roomlog.room.dto.CreateRoomResponse;
import com.roomlog.room.dto.DeleteRoomResponse;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.UpdateRoomRequest;
import com.roomlog.room.dto.UpdateRoomResponse;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ScanRepository scanRepository;
    private final HouseRepository houseRepository;
    private final AnalysisRepository analysisRepository;
    private final DefectRepository defectRepository;
    private final EstimateRepository estimateRepository;
    private final RepairRepository repairRepository;

    // S05: POST /houses/{houseId}/rooms
    @Transactional
    public CreateRoomResponse createRoom(Long userId, Long houseId, CreateRoomRequest request) {
        houseRepository.findByIdAndUserId(houseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_001));

        Scan scan = scanRepository.findById(request.getScanId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCAN_001));

        if (!scan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMON_403);
        }

        if (scan.getStatus() != Scan.Status.COMPLETED) {
            throw new CustomException(ErrorCode.SCAN_004);
        }

        if (scan.getRoomId() != null) {
            throw new CustomException(ErrorCode.COMMON_400, "이미 방에 연결된 스캔입니다.");
        }

        Room room = Room.builder()
                .houseId(houseId)
                .name(request.getName())
                .moveInDate(LocalDate.now())
                .plyUrl(scan.getPlyUrl())
                .build();
        roomRepository.save(room);

        scan.assignRoom(room.getId());

        return CreateRoomResponse.of(room, scan);
    }

    @Transactional(readOnly = true)
    public GetRoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        Scan latestScan = scanRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId)
                .orElse(null);

        return GetRoomDetailResponse.of(room, latestScan);
    }

    @Transactional
    public UpdateRoomResponse updateRoom(Long userId, Long roomId, UpdateRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        if (request.getMoveOutDate() != null && request.getMoveInDate() != null
                && request.getMoveInDate().isAfter(request.getMoveOutDate())) {
            throw new CustomException(ErrorCode.COMMON_400, "입주일은 퇴거일보다 이후일 수 없습니다.");
        }

        room.update(request.getName(), request.getMoveInDate(), request.getMoveOutDate());

        return UpdateRoomResponse.from(room);
    }

    @Transactional
    public DeleteRoomResponse deleteRoom(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        List<Scan> scans = scanRepository.findByRoomId(roomId);
        scans.forEach(Scan::softDelete);

        List<Analysis> analyses = analysisRepository.findByRoomId(roomId);
        List<Long> analysisIds = analyses.stream().map(Analysis::getId).toList();
        analyses.forEach(Analysis::softDelete);

        if (!analysisIds.isEmpty()) {
            defectRepository.findByAnalysisIdIn(analysisIds).forEach(Defect::softDelete);
        }

        estimateRepository.findByRoomId(roomId).forEach(e -> e.softDelete());
        repairRepository.findByRoomId(roomId).forEach(r -> r.softDelete());

        room.softDelete();

        return DeleteRoomResponse.of(roomId, null);
    }
}
