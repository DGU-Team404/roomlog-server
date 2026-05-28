package com.roomlog.scan.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.AiClient;
import com.roomlog.global.infra.R2FileUploader;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.scan.domain.Scan;

import com.roomlog.scan.dto.AiReconstructionRequest;
import com.roomlog.scan.dto.AiReconstructionResult;
import com.roomlog.scan.dto.CreateScanRequest;
import com.roomlog.scan.dto.CreateScanResponse;
import com.roomlog.scan.dto.GetScanResponse;
import com.roomlog.scan.dto.GetScanStatusResponse;
import com.roomlog.scan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final HouseRepository houseRepository;
    private final R2FileUploader r2FileUploader;
    private final AiClient aiClient;

    @Transactional
    public CreateScanResponse uploadScan(Long userId, MultipartFile file, CreateScanRequest request) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.COMMON_400, "스캔 파일이 없습니다.");
        }

        houseRepository.findByIdAndUserId(request.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_403));

        Scan scan = Scan.builder()
                .userId(userId)
                .houseId(request.getHouseId())
                .status(Scan.Status.SCANNING)
                .build();
        scanRepository.save(scan);

        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".zip";
        String key = "scans/" + scan.getId() + "/model" + extension;
        String fileUrl = r2FileUploader.upload(file, key);
        scan.updateFileUrl(fileUrl);

        try {
            r2FileUploader.verifyAccessible(fileUrl);
            aiClient.requestReconstruction(new AiReconstructionRequest(
                    scan.getId(), fileUrl, aiClient.scanCallbackUrl(scan.getId())));
        } catch (Exception e) {
            scan.fail();
        }

        return CreateScanResponse.from(scan);
    }

    @Transactional(readOnly = true)
    public GetScanResponse getScanPreview(Long userId, Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCAN_001));

        if (!scan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMON_403);
        }

        if (scan.getStatus() != Scan.Status.COMPLETED) {
            throw new CustomException(ErrorCode.SCAN_004);
        }

        return GetScanResponse.from(scan);
    }

@Transactional
    public void receiveReconstructionResult(Long scanId, AiReconstructionResult result) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCAN_001));

        if (scan.getStatus() != Scan.Status.SCANNING) {
            throw new CustomException(ErrorCode.SCAN_004);
        }

        scan.complete(result.getScanUrl());
    }

    @Transactional
    public void cancelScan(Long userId, Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCAN_001));

        if (!scan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMON_403);
        }

        if (scan.getStatus() != Scan.Status.SCANNING) {
            throw new CustomException(ErrorCode.SCAN_005);
        }

        scan.fail();
    }

    @Transactional(readOnly = true)
    public GetScanStatusResponse getScanStatus(Long userId, Long scanId) {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCAN_001));

        if (!scan.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMON_403);
        }

        return GetScanStatusResponse.from(scan);
    }
}
