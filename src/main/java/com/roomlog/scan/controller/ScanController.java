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

        CreateScanResponse response = scanService.uploadScan(loginUser.userId(), file, new CreateScanRequest(scanType));
        return ApiResponse.success(201, "스캔 업로드에 성공했습니다.", response);
    }

    @Operation(
            summary = "S07. 스캔 미리보기",
            description = """
                    스캔 ID로 3D 스캔 결과 파일 정보를 조회합니다.

                    - 스캔 상태가 COMPLETED인 경우에만 조회 가능합니다.
                    - 상태가 COMPLETED가 아닌 경우 400(SCAN_004) 에러가 반환됩니다.
                    - 존재하지 않는 scanId 요청 시 404(SCAN_001) 에러가 반환됩니다.
                    """
    )
    @GetMapping("/{scanId}/preview")
    public ApiResponse<GetScanResponse> getScanPreview(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 스캔 ID", example = "12") @PathVariable Long scanId) {

        GetScanResponse response = scanService.getScanPreview(loginUser.userId(), scanId);
        return ApiResponse.success(200, "스캔 결과 조회에 성공했습니다.", response);
    }

    @Operation(
            summary = "V01. 3D Viewer",
            description = """
                    스캔 ID로 .ply 기반 3D 파일 경로를 조회합니다.

                    - 스캔 상태가 COMPLETED인 경우에만 조회 가능합니다.
                    - 상태가 COMPLETED가 아닌 경우 400(SCAN_004) 에러가 반환됩니다.
                    - 존재하지 않는 scanId 요청 시 404(SCAN_001) 에러가 반환됩니다.
                    """,
            tags = "3. Viewer"
    )
    @GetMapping("/{scanId}/viewer")
    public ApiResponse<GetScanResponse> getScanViewer(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 스캔 ID", example = "12") @PathVariable Long scanId) {

        GetScanResponse response = scanService.getScanPreview(loginUser.userId(), scanId);
        return ApiResponse.success(200, "3D 스캔 결과 조회에 성공했습니다.", response);
    }

    @Operation(
            summary = "S06. 스캔 상태 조회",
            description = "스캔 ID로 현재 스캔의 처리 상태를 조회합니다. 상태는 SCANNING / COMPLETED / FAILED 중 하나입니다."
    )
    @GetMapping("/{scanId}/status")
    public ApiResponse<GetScanStatusResponse> getScanStatus(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 스캔 ID", example = "12") @PathVariable Long scanId) {

        GetScanStatusResponse response = scanService.getScanStatus(loginUser.userId(), scanId);
        return ApiResponse.success(200, "스캔 상태 조회에 성공했습니다.", response);
    }
}
