package com.roomlog.scan.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.dto.CreateScanRequest;
import com.roomlog.scan.dto.CreateScanResponse;
import com.roomlog.scan.dto.GetScanResponse;
import com.roomlog.scan.dto.GetScanStatusResponse;
import com.roomlog.scan.service.ScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @Operation(summary = "S04. 스캔 업로드", description = "LiDAR 스캔 결과 파일(.ply)을 서버에 업로드합니다. 업로드 직후 상태는 SCANNING이며, 처리 완료 시 COMPLETED로 변경됩니다.", tags = "3. Scan")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CreateScanResponse> uploadScan(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "스캔할 집 ID", example = "1")
            @RequestParam("house_id") Long houseId,
            @Parameter(description = "연결할 방 ID (OUT 스캔 시 필수)", example = "3")
            @RequestParam(value = "room_id", required = false) Long roomId,
            @Parameter(description = "스캔 타입 (IN: 입주, OUT: 퇴거)", example = "IN")
            @RequestParam("scan_type") Scan.ScanType scanType) {

        CreateScanResponse response = scanService.uploadScan(loginUser.userId(), file, new CreateScanRequest(houseId, roomId, scanType));
        return ApiResponse.success(201, "스캔 업로드에 성공했습니다.", response);
    }

    @Operation(summary = "S06. 스캔 상태 조회", description = "스캔 ID로 현재 처리 상태를 조회합니다. 상태는 SCANNING / COMPLETED / FAILED 중 하나입니다.", tags = "3. Scan")
    @GetMapping("/{scanId}/status")
    public ApiResponse<GetScanStatusResponse> getScanStatus(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 스캔 ID", example = "12") @PathVariable Long scanId) {

        GetScanStatusResponse response = scanService.getScanStatus(loginUser.userId(), scanId);
        return ApiResponse.success(200, "스캔 상태 조회에 성공했습니다.", response);
    }

    @Operation(summary = "S07. 스캔 미리보기", description = "스캔 ID로 3D 파일 경로와 메타데이터를 조회합니다. COMPLETED 상태의 스캔만 조회할 수 있습니다.", tags = "3. Scan")
    @GetMapping("/{scanId}/preview")
    public ApiResponse<GetScanResponse> getScanPreview(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 스캔 ID", example = "12") @PathVariable Long scanId) {

        GetScanResponse response = scanService.getScanPreview(loginUser.userId(), scanId);
        return ApiResponse.success(200, "스캔 결과 조회에 성공했습니다.", response);
    }
}
