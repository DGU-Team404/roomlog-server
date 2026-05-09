package com.roomlog.estimate.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.estimate.domain.Estimate;
import com.roomlog.estimate.domain.EstimateDefect;
import com.roomlog.estimate.dto.CreateEstimateRequest;
import com.roomlog.estimate.dto.CreateEstimateResponse;
import com.roomlog.estimate.dto.EstimateListItemResponse;
import com.roomlog.estimate.dto.EstimatePreviewRequest;
import com.roomlog.estimate.dto.EstimatePreviewResponse;
import com.roomlog.estimate.dto.GetEstimateDetailResponse;
import com.roomlog.estimate.dto.GetEstimateListResponse;
import com.roomlog.estimate.repository.EstimateDefectRepository;
import com.roomlog.estimate.repository.EstimateRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.KakaoLocalClient;
import com.roomlog.global.infra.KakaoLocalClient.KakaoPlace;
import com.roomlog.global.infra.SmsService;
import com.roomlog.house.repository.HouseRepository;
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
public class EstimateService {

    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final DefectRepository defectRepository;
    private final EstimateRepository estimateRepository;
    private final EstimateDefectRepository estimateDefectRepository;
    private final KakaoLocalClient kakaoLocalClient;
    private final SmsService smsService;

    @Transactional(readOnly = true)
    public GetEstimateListResponse getEstimateList(Long userId) {
        List<Estimate> estimates = estimateRepository.findByUserId(userId);

        List<Long> estimateIds = estimates.stream().map(Estimate::getId).toList();
        Map<Long, List<EstimateDefect>> defectsByEstimateId = estimateDefectRepository
                .findByEstimateIdIn(estimateIds).stream()
                .collect(Collectors.groupingBy(EstimateDefect::getEstimateId));

        List<EstimateListItemResponse> items = estimates.stream()
                .map(e -> EstimateListItemResponse.of(e, defectsByEstimateId.getOrDefault(e.getId(), List.of())))
                .toList();

        return GetEstimateListResponse.of(items);
    }

    @Transactional(readOnly = true)
    public GetEstimateDetailResponse getEstimateDetail(Long userId, Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTIMATE_002));

        if (!estimate.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ESTIMATE_003);
        }

        List<EstimateDefect> estimateDefects = estimateDefectRepository.findByEstimateId(estimateId);
        return GetEstimateDetailResponse.of(estimate, estimateDefects);
    }

    @Transactional(readOnly = true)
    public EstimatePreviewResponse previewEstimate(Long userId, EstimatePreviewRequest request) {
        Analysis analysis = analysisRepository.findById(request.getAnalysisId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        KakaoPlace place = kakaoLocalClient.getPlaceById(request.getProviderExternalId());
        if (place == null) {
            throw new CustomException(ErrorCode.REPAIRSHOP_002);
        }

        List<Defect> defects = defectRepository.findByAnalysisId(request.getAnalysisId());

        return EstimatePreviewResponse.of(analysis.getId(), room.getId(), place, defects, request.getMessage());
    }

    @Transactional
    public CreateEstimateResponse createEstimate(Long userId, CreateEstimateRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        analysisRepository.findById(request.getAnalysisId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        List<Defect> defects = defectRepository.findAllById(request.getDefectIds());
        if (defects.size() != request.getDefectIds().size()) {
            throw new CustomException(ErrorCode.DEFECT_001);
        }

        try {
            Estimate estimate = Estimate.builder()
                    .userId(userId)
                    .roomId(request.getRoomId())
                    .analysisId(request.getAnalysisId())
                    .providerName(request.getProviderName())
                    .providerPhone(request.getProviderPhone())
                    .providerAddress(request.getProviderAddress())
                    .message(request.getMessage())
                    .build();
            estimateRepository.save(estimate);

            List<EstimateDefect> estimateDefects = defects.stream()
                    .map(d -> EstimateDefect.builder()
                            .estimateId(estimate.getId())
                            .defectId(d.getId())
                            .build())
                    .toList();
            estimateDefectRepository.saveAll(estimateDefects);

            if (request.getProviderPhone() != null && !request.getProviderPhone().isBlank()) {
                String smsText = buildSmsText(defects.size(), totalCost(defects), request.getMessage());
                boolean sent = smsService.send(request.getProviderPhone(), smsText);
                estimate.updateStatus(sent ? Estimate.Status.SENT : Estimate.Status.FAILED);
            }

            return CreateEstimateResponse.of(estimate.getId());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ESTIMATE_001);
        }
    }

    private String buildSmsText(int defectCount, int totalCost, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요. RoomLog를 통해 문의드립니다.\n");
        sb.append(String.format("현재 총 %d건의 하자가 확인되었으며 예상 수리비는 약 %,d원입니다.\n", defectCount, totalCost));
        if (userMessage != null && !userMessage.isBlank()) {
            sb.append(userMessage);
        }
        return sb.toString();
    }

    private int totalCost(List<Defect> defects) {
        return defects.stream().mapToInt(Defect::getEstimatedCost).sum();
    }
}
