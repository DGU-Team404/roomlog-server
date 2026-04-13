package com.roomlog.analysis.service;

import com.roomlog.analysis.domain.Analysis;
import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisResponse;
import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.dto.DefectItemResponse;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final RoomRepository roomRepository;
    private final ScanRepository scanRepository;
    private final DefectRepository defectRepository;

    @Transactional(readOnly = true)
    public GetAnalysisResponse getAnalysis(Long userId, Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_001));

        Room room = roomRepository.findById(analysis.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        if (analysis.getStatus() != Analysis.Status.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_004);
        }

        List<DefectItemResponse> defects = defectRepository.findByAnalysisId(analysisId)
                .stream()
                .map(DefectItemResponse::from)
                .toList();

        return GetAnalysisResponse.of(analysis, defects);
    }

    @Transactional
    public CreateAnalysisResponse createAnalysis(Long userId, CreateAnalysisRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        Scan inScan = scanRepository.findById(request.getInScanId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_003));

        Scan outScan = scanRepository.findById(request.getOutScanId())
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_003));

        if (!inScan.getUserId().equals(userId) || !outScan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
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

        return CreateAnalysisResponse.of(analysis, inScan, outScan);
    }
}