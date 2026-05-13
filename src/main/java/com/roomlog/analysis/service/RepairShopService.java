package com.roomlog.analysis.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.dto.GetRepairShopsResponse;
import com.roomlog.analysis.dto.RepairShopResponse;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.KakaoLocalClient;
import com.roomlog.global.infra.KakaoLocalClient.KakaoPlace;
import com.roomlog.house.domain.House;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RepairShopService {

    private static final Map<String, String> TYPE_KEYWORD_MAP = Map.of(
            "SCRATCH", "스크래치 수리 업체",
            "CRACK", "균열 보수 업체",
            "PEELING", "도배 업체",
            "STAIN", "도배 청소 업체",
            "BREAKAGE", "파손 수리 업체"
    );

    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final KakaoLocalClient kakaoLocalClient;

    @Transactional(readOnly = true)
    public GetRepairShopsResponse getRepairShops(Long userId, Long analysisId, String type, String radius, String sort) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        House house = houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        List<RepairShopResponse> repairShops = searchNearbyShops(house.getAddress(), type, radius, sort);
        return GetRepairShopsResponse.of(analysisId, type, radius, sort, repairShops);
    }

    @Transactional(readOnly = true)
    public GetRepairShopsResponse getRepairShopsByRoom(Long userId, Long roomId, String type, String radius, String sort) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        House house = houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        List<RepairShopResponse> repairShops = searchNearbyShops(house.getAddress(), type, radius, sort);
        return GetRepairShopsResponse.ofRoom(roomId, type, radius, sort, repairShops);
    }

    private List<RepairShopResponse> searchNearbyShops(String address, String type, String radius, String sort) {
        double[] coords = kakaoLocalClient.geocodeAddress(address);
        if (coords == null) {
            throw new CustomException(ErrorCode.REPAIRSHOP_003);
        }

        String keyword = TYPE_KEYWORD_MAP.getOrDefault(
                type != null ? type.toUpperCase() : "", "인테리어 수리 업체");
        int radiusMeters = parseRadiusToMeters(radius);
        String sortParam = (sort != null && sort.equals("accuracy")) ? "accuracy" : "distance";

        List<KakaoPlace> places = kakaoLocalClient.searchKeyword(keyword, coords[0], coords[1], radiusMeters, sortParam);

        if (places == null || places.isEmpty()) {
            throw new CustomException(ErrorCode.REPAIRSHOP_001);
        }

        return places.stream()
                .map(RepairShopResponse::from)
                .toList();
    }

    private int parseRadiusToMeters(String radius) {
        if (radius == null || radius.isBlank()) return 3000;
        String lower = radius.toLowerCase().trim();
        if (lower.endsWith("km")) {
            return (int) (Double.parseDouble(lower.replace("km", "")) * 1000);
        }
        if (lower.endsWith("m")) {
            return Integer.parseInt(lower.replace("m", ""));
        }
        return 3000;
    }
}
