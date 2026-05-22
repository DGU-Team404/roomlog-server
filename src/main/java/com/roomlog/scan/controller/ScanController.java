package com.roomlog.scan.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.scan.dto.AiReconstructionResult;
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

    @Operation(summary = "AI reconstruction 결과 수신 (내부 전용)", description = "AI 서버가 reconstruction 완료 후 결과를 전송하는 내부 API입니다. X-Api-Key 헤더 인증이 필요합니다.", tags = "0. Internal")
    @PostMapping("/{scanId}/result")
    public ApiResponse<Void> receiveReconstructionResult(
            @Parameter(description = "스캔 ID") @PathVariable Long scanId,
            @RequestBody AiReconstructionResult result) {

        scanService.receiveReconstructionResult(scanId, result);
        return ApiResponse.success(200, "reconstruction 결과가 반영되었습니다.", null);
    }

    @Operation(summary = "S04. 스캔 업로드", description = "LiDAR 스캔 결과 파일(.ply)을 서버에 업로드합니다. 업로드 직후 상태는 SCANNING이며, 처리 완료 시 COMPLETED로 변경됩니다. 스캔 업로드 시점에는 IN/OUT 타입을 지정하지 않습니다. 타입은 이후 분석(Analysis) 생성 시 결정됩니다.", tags = "3. Scan")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CreateScanResponse> uploadScan(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "스캔할 집 ID", example = "1")
            @RequestParam("house_id") Long houseId) {

        CreateScanResponse response = scanService.uploadScan(loginUser.userId(), file, new CreateScanRequest(houseId));
        return ApiResponse.success(201, "스캔 업로드에 성공했습니다.", response);
    }

    @Operation(summary = "S05. 스캔 취소", description = "업로드된 스캔의 3D 재구성을 취소합니다. SCANNING 상태인 스캔만 취소할 수 있으며, 취소 시 상태가 FAILED로 변경됩니다.", tags = "3. Scan")
    @PostMapping("/{scanId}/cancel")
    public ApiResponse<Void> cancelScan(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "취소할 스캔 ID", example = "12") @PathVariable Long scanId) {

        scanService.cancelScan(loginUser.userId(), scanId);
        return ApiResponse.success(200, "스캔이 취소되었습니다.", null);
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
