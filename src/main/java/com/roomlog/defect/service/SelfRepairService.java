package com.roomlog.defect.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.domain.DefectRepairGuide;
import com.roomlog.defect.domain.SelfRepairPolicy;
import com.roomlog.defect.dto.GetSelfRepairResponse;
import com.roomlog.defect.dto.RepairItem;
import com.roomlog.defect.repository.DefectRepairGuideRepository;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.GptClient;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SelfRepairService {

    private static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query=";
    private static final String COUPANG_SEARCH_URL = "https://www.coupang.com/np/search?q=";
    private static final String GMARKET_SEARCH_URL = "https://browse.gmarket.co.kr/search?keyword=";

    private final DefectRepository defectRepository;
    private final DefectRepairGuideRepository defectRepairGuideRepository;
    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final GptClient gptClient;

    @Transactional
    public GetSelfRepairResponse getSelfRepairGuide(Long userId, Long defectId) {
        Defect defect = defectRepository.findById(defectId)
                .orElseThrow(() -> new CustomException(ErrorCode.DEFECT_001));

        validateOwnership(userId, defect);

        DefectRepairGuide guide = defectRepairGuideRepository.findById(defectId)
                .orElseGet(() -> defectRepairGuideRepository.save(generate(defect)));

        return GetSelfRepairResponse.from(guide);
    }

    private DefectRepairGuide generate(Defect defect) {
        boolean possible = SelfRepairPolicy.isSelfRepairable(
                defect.getType(), defect.getSeverity(), defect.getArea());

        GptClient.SelfRepairGuide generated = gptClient.generateSelfRepairGuide(
                defect.getType(), defect.getSeverity(), defect.getLocation(),
                defect.getArea(), defect.getDescription(), possible);

        if (!possible) {
            return DefectRepairGuide.builder()
                    .defectId(defect.getId())
                    .selfRepairPossible(false)
                    .description(generated.getDescription())
                    .videoUrl(null)
                    .items(List.of())
                    .totalCost(0)
                    .build();
        }

        List<RepairItem> items = generated.getItems() == null ? List.of()
                : generated.getItems().stream()
                        .map(item -> new RepairItem(
                                item.getName(),
                                COUPANG_SEARCH_URL + encode(item.getSearchQuery()),
                                GMARKET_SEARCH_URL + encode(item.getSearchQuery()),
                                item.getEstimatedPrice() != null ? item.getEstimatedPrice() : 0))
                        .toList();

        int totalCost = items.stream().mapToInt(RepairItem::getLowestPrice).sum();

        return DefectRepairGuide.builder()
                .defectId(defect.getId())
                .selfRepairPossible(true)
                .description(generated.getDescription())
                .videoUrl(videoUrl(generated.getVideoSearchQuery()))
                .items(items)
                .totalCost(totalCost)
                .build();
    }

    private void validateOwnership(Long userId, Defect defect) {
        Analysis analysis = analysisRepository.findById(defect.getAnalysisId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));
    }

    private String videoUrl(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) return null;
        return YOUTUBE_SEARCH_URL + encode(searchQuery);
    }

    private String encode(String keyword) {
        return URLEncoder.encode(keyword != null ? keyword : "", StandardCharsets.UTF_8);
    }
}
