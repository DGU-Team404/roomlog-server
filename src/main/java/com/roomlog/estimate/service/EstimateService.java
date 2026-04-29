package com.roomlog.estimate.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.estimate.dto.EstimatePreviewRequest;
import com.roomlog.estimate.dto.EstimatePreviewResponse;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.KakaoLocalClient;
import com.roomlog.global.infra.KakaoLocalClient.KakaoPlace;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final DefectRepository defectRepository;
    private final KakaoLocalClient kakaoLocalClient;

    @Transactional(readOnly = true)
    public EstimatePreviewResponse previewEstimate(Long userId, EstimatePreviewRequest request) {
        Analysis analysis = analysisRepository.findById(request.getAnalysisId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        KakaoPlace place = kakaoLocalClient.getPlaceById(request.getProviderExternalId());
        if (place == null) {
            throw new CustomException(ErrorCode.REPAIRSHOP_002);
        }

        List<Defect> defects = defectRepository.findByAnalysisId(request.getAnalysisId());

        return EstimatePreviewResponse.of(analysis.getId(), room.getId(), place, defects, request.getMessage());
    }
}
