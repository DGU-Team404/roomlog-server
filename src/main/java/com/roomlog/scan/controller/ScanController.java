package com.roomlog.scan.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.dto.CreateScanRequest;
import com.roomlog.scan.dto.CreateScanResponse;
import com.roomlog.scan.service.ScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "4. 스캔", description = "3D 스캔 업로드, 상태 조회, 미리보기 API")
@RestController
@RequestMapping("/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @Operation(summary = "S04. 스캔 업로드", description = "LiDAR 스캔 결과 파일과 메타데이터를 서버에 업로드합니다. (임시 저장)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CreateScanResponse> uploadScan(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestPart("file") MultipartFile file,
            @RequestParam("scan_type") Scan.ScanType scanType) {

        CreateScanResponse response = scanService.uploadScan(file, new CreateScanRequest(scanType));
        return ApiResponse.success(201, "스캔 업로드에 성공했습니다.", response);
    }
}
