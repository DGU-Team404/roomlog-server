package com.roomlog.analysis.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.dto.AiCompareRequest;
import com.roomlog.analysis.dto.AiResultRequest;
import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisCostResponse;
import com.roomlog.analysis.dto.GetAnalysisResponse;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.domain.DefectUnitPrice;
import com.roomlog.defect.domain.SeverityMultiplier;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.dto.DefectItemResponse;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.defect.repository.DefectUnitPriceRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.AiClient;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final ScanRepository scanRepository;
    private final DefectRepository defectRepository;
    private final DefectUnitPriceRepository defectUnitPriceRepository;
    private final AiClient aiClient;

    @Transactional(readOnly = true)
    public GetAnalysisResponse getAnalysis(Long userId, Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        if (analysis.getStatus() != Analysis.Status.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_004);
        }

        List<DefectItemResponse> defects = defectRepository.findByAnalysisId(analysisId)
                .stream()
                .map(DefectItemResponse::from)
                .toList();

        return GetAnalysisResponse.of(analysis, room.getFileUrl(), defects);
    }

    @Transactional(readOnly = true)
    public GetAnalysisCostResponse getAnalysisCost(Long userId, Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        if (analysis.getStatus() != Analysis.Status.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_004);
        }

        List<Defect> defects = defectRepository.findByAnalysisId(analysisId);

        return GetAnalysisCostResponse.of(analysisId, defects);
    }

    @Transactional
    public void receiveAiResult(Long analysisId, AiResultRequest request) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        if (analysis.getStatus() != Analysis.Status.PENDING) {
            throw new CustomException(ErrorCode.ANALYSIS_002);
        }

        if (!request.isSuccess()) {
            analysis.fail();
            return;
        }

        List<Defect> defects = request.getDefects().stream()
                .map(item -> {
                    DefectUnitPrice unitPrice = defectUnitPriceRepository.findById(item.getType())
                            .orElseThrow(() -> new CustomException(ErrorCode.COMMON_400));

                    SeverityMultiplier severity = SeverityMultiplier.valueOf(item.getSeverity());
                    int estimatedCost = (int) Math.ceil(unitPrice.getUnitPrice() * item.getArea() * severity.getMultiplier());

                    return Defect.builder()
                            .analysisId(analysisId)
                            .type(item.getType())
                            .severity(item.getSeverity())
                            .location(item.getLocation())
                            .area(item.getArea())
                            .estimatedCost(estimatedCost)
                            .description(item.getDescription())
                            .region3d(item.getRegion3d())
                            .build();
                })
                .toList();

        defectRepository.saveAll(defects);

        int totalCost = defects.stream().mapToInt(Defect::getEstimatedCost).sum();
        analysis.complete(totalCost);
    }

    @Transactional
    public CreateAnalysisResponse createAnalysis(Long userId, CreateAnalysisRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        Scan inScan = scanRepository.findById(request.getInScanId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_003));

        Scan outScan = scanRepository.findById(request.getOutScanId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_003));

        if (!inScan.getUserId().equals(userId) || !outScan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMON_403);
        }

        if (inScan.getScanType() != Scan.ScanType.IN || outScan.getScanType() != Scan.ScanType.OUT) {
            throw new CustomException(ErrorCode.ANALYSIS_003);
        }

        if (inScan.getStatus() != Scan.Status.COMPLETED || outScan.getStatus() != Scan.Status.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_003);
        }

        Analysis analysis = Analysis.builder()
                .roomId(request.getRoomId())
                .inScanId(request.getInScanId())
                .outScanId(request.getOutScanId())
                .build();
        analysisRepository.save(analysis);

        List<AiCompareRequest.DefectItem> inDefects = analysisRepository
                .findFirstByInScanIdAndStatusOrderByCreatedAtDesc(inScan.getId(), Analysis.Status.COMPLETED)
                .map(prev -> defectRepository.findByAnalysisId(prev.getId()).stream()
                        .map(d -> new AiCompareRequest.DefectItem(
                                d.getType(), d.getSeverity(), d.getLocation(),
                                d.getArea(), d.getDescription(), d.getRegion3d()))
                        .toList())
                .orElse(Collections.emptyList());

        List<AiCompareRequest.DefectItem> outDefects = analysisRepository
                .findFirstByInScanIdAndStatusOrderByCreatedAtDesc(outScan.getId(), Analysis.Status.COMPLETED)
                .map(prev -> defectRepository.findByAnalysisId(prev.getId()).stream()
                        .map(d -> new AiCompareRequest.DefectItem(
                                d.getType(), d.getSeverity(), d.getLocation(),
                                d.getArea(), d.getDescription(), d.getRegion3d()))
                        .toList())
                .orElse(Collections.emptyList());

        aiClient.requestDefectComparison(new AiCompareRequest(
                analysis.getId(),
                inScan.getId(), inScan.getFileUrl(), inDefects,
                outScan.getId(), outScan.getFileUrl(), outDefects));

        return CreateAnalysisResponse.of(analysis, inScan, outScan);
    }
}