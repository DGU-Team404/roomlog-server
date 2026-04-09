package com.roomlog.scan.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.R2FileUploader;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.dto.CreateScanRequest;
import com.roomlog.scan.dto.CreateScanResponse;
import com.roomlog.scan.repository.ScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final R2FileUploader r2FileUploader;

    @Transactional
    public CreateScanResponse uploadScan(MultipartFile file, CreateScanRequest request) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.COMMON_400, "스캔 파일이 없습니다.");
        }

        Scan scan = Scan.builder()
                .scanType(request.getScanType())
                .status(Scan.Status.SCANNING)
                .build();
        scanRepository.save(scan);

        String key = "scans/" + scan.getId() + "/model.ply";
        String fileUrl = r2FileUploader.upload(file, key);
        scan.updateFileUrl(fileUrl);

        return CreateScanResponse.from(scan);
    }
}
