package com.roomlog.house.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.estimate.repository.EstimateRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.house.domain.House;
import com.roomlog.house.dto.CreateHouseRequest;
import com.roomlog.house.dto.CreateHouseResponse;
import com.roomlog.house.dto.DeleteHouseResponse;
import com.roomlog.house.dto.GetHouseRoomsResponse;
import com.roomlog.house.dto.GetHousesResponse;
import com.roomlog.house.dto.HouseListItemResponse;
import com.roomlog.house.dto.SetMainHouseResponse;
import com.roomlog.house.dto.UpdateHouseRequest;
import com.roomlog.house.dto.UpdateHouseResponse;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.repair.repository.RepairRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.repository.ScanRepository;
import com.roomlog.user.domain.User;
import com.roomlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ScanRepository scanRepository;
    private final AnalysisRepository analysisRepository;
    private final DefectRepository defectRepository;
    private final EstimateRepository estimateRepository;
    private final RepairRepository repairRepository;

    // H00: 집 생성
    @Transactional
    public CreateHouseResponse createHouse(Long userId, CreateHouseRequest request) {
        House house = House.builder()
                .userId(userId)
                .name(request.getName())
                .address(request.getAddress())
                .houseColor(request.getHouseColor())
                .floorColor(request.getFloorColor())
                .build();
        houseRepository.save(house);

        return CreateHouseResponse.from(house);
    }

    // H01: 집 목록 조회 (대표 집 포함)
    @Transactional(readOnly = true)
    public GetHousesResponse getHouses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));

        List<HouseListItemResponse> houseItems = houseRepository.findByUserId(userId).stream()
                .map(HouseListItemResponse::from)
                .toList();

        House mainHouse = user.getMainHouseId() != null
                ? houseRepository.findByIdAndUserId(user.getMainHouseId(), userId).orElse(null)
                : houseRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);

        return GetHousesResponse.of(mainHouse, houseItems);
    }

    // H02: 대표 집 설정
    @Transactional
    public SetMainHouseResponse setMainHouse(Long userId, Long houseId) {
        houseRepository.findByIdAndUserId(houseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_001));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));

        user.updateMainHouseId(houseId);

        return SetMainHouseResponse.of(houseId);
    }

    // H03: 집 이름 수정
    @Transactional
    public UpdateHouseResponse updateHouse(Long userId, Long houseId, UpdateHouseRequest request) {
        House house = houseRepository.findByIdAndUserId(houseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_001));

        house.update(request.getName(), request.getAddress(), request.getHouseColor(), request.getFloorColor());

        return UpdateHouseResponse.from(house);
    }

    // H04: 집 삭제 (cascade soft delete)
    @Transactional
    public DeleteHouseResponse deleteHouse(Long userId, Long houseId) {
        House house = houseRepository.findByIdAndUserId(houseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_001));

        List<Room> rooms = roomRepository.findByHouseId(houseId);
        List<Long> roomIds = rooms.stream().map(Room::getId).toList();

        if (!roomIds.isEmpty()) {
            scanRepository.findByRoomIdIn(roomIds).forEach(Scan::softDelete);

            List<Analysis> analyses = analysisRepository.findByRoomIdIn(roomIds);
            List<Long> analysisIds = analyses.stream().map(Analysis::getId).toList();
            analyses.forEach(Analysis::softDelete);

            if (!analysisIds.isEmpty()) {
                defectRepository.findByAnalysisIdIn(analysisIds).forEach(Defect::softDelete);
            }

            estimateRepository.findByRoomIdIn(roomIds).forEach(e -> e.softDelete());
            repairRepository.findByRoomIdIn(roomIds).forEach(r -> r.softDelete());
            rooms.forEach(Room::softDelete);
        }

        house.softDelete();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));

        // 명시 설정된 대표 집을 삭제한 경우 → 초기화
        if (houseId.equals(user.getMainHouseId())) {
            user.updateMainHouseId(null);
        }

        // 실제 메인 집 계산: 명시 설정이 없으면 최근 생성된 집
        Long newMainHouseId = user.getMainHouseId() != null
                ? user.getMainHouseId()
                : houseRepository.findTopByUserIdOrderByCreatedAtDesc(userId).map(House::getId).orElse(null);

        return DeleteHouseResponse.of(houseId, newMainHouseId);
    }

    // H05: 집 내부 방 목록 조회
    @Transactional(readOnly = true)
    public GetHouseRoomsResponse getHouseRooms(Long userId, Long houseId) {
        House house = houseRepository.findByIdAndUserId(houseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOUSE_001));

        List<GetHouseRoomsResponse.RoomItem> roomItems = roomRepository.findByHouseId(houseId).stream()
                .map(room -> {
                    Scan latestScan = scanRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);
                    return GetHouseRoomsResponse.RoomItem.of(room, latestScan);
                })
                .toList();

        return GetHouseRoomsResponse.of(house.getId(), house.getName(), roomItems);
    }
}