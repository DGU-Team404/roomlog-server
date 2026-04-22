package com.roomlog.scan.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.R2FileUploader;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;

import java.util.List;
import com.roomlog.scan.dto.CreateScanRequest;
import com.roomlog.scan.dto.CreateScanResponse;
import com.roomlog.scan.dto.GetRoomScansResponse;
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
    private final RoomRepository roomRepository;
    private final R2FileUploader r2FileUploader;

    @Transactional
    public CreateScanResponse uploadScan(Long userId, MultipartFile file, CreateScanRequest request) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.COMMON_400, "스캔 파일이 없습니다.");
        }

        Scan scan = Scan.builder()
                .userId(userId)
                .scanType(request.getScanType())
                .status(Scan.Status.SCANNING)
                .build();
        scanRepository.save(scan);

        String key = "scans/" + scan.getId() + "/model.ply";
        String fileUrl = r2FileUploader.upload(file, key);
        scan.updateFileUrl(fileUrl);

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

    @Transactional(readOnly = true)
    public GetRoomScansResponse getRoomScans(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        List<Scan> scans = scanRepository.findByRoomId(roomId);
        return GetRoomScansResponse.of(room, scans);
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
