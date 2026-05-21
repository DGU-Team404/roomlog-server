package com.roomlog.analysis.controller;

import com.roomlog.analysis.dto.AiResultRequest;
import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisResponse;
import com.roomlog.analysis.service.AnalysisService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "AI 분석 결과 수신 (내부 전용)", description = "AI 서버가 분석 완료 후 하자 목록을 전송하는 내부 API입니다. X-Api-Key 헤더 인증이 필요합니다.", tags = "0. Internal")
    @PostMapping("/{analysisId}/result")
    public ApiResponse<Void> receiveAiResult(
            @Parameter(description = "분석 ID") @PathVariable Long analysisId,
            @RequestBody AiResultRequest request) {

        analysisService.receiveAiResult(analysisId, request);
        return ApiResponse.success(200, "분석 결과가 반영되었습니다.", null);
    }

    @Operation(summary = "V02-1. 분석 생성", description = """
            스캔 데이터를 기반으로 하자 분석을 생성합니다. 분석은 PENDING 상태로 생성되며, AI 처리 완료 후 COMPLETED로 변경됩니다.

            [단일 스캔 분석] in_scan_id만 전달하면 해당 스캔 하나로 하자를 탐지합니다. 방 스캔 직후 바로 하자를 확인할 때 사용합니다. out_scan_id는 생략하거나 null로 보내세요.

            [두 방 비교 분석] in_scan_id(기준 방 스캔)와 out_scan_id(비교 방 스캔)를 모두 전달하면 두 스캔을 비교하여 새로 생긴 하자를 탐지합니다. 입주 전 상태와 퇴거 후 상태를 비교할 때 사용합니다.

            스캔의 IN/OUT 역할은 스캔 업로드 시점이 아니라 이 API를 호출할 때 결정됩니다. in_scan_id에 넣은 스캔이 IN, out_scan_id에 넣은 스캔이 OUT으로 처리됩니다.""", tags = "4. Viewer")
    @PostMapping
    public ApiResponse<CreateAnalysisResponse> createAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateAnalysisRequest request) {

        CreateAnalysisResponse response = analysisService.createAnalysis(loginUser.userId(), request);
        return ApiResponse.success(201, "하자 분석 생성에 성공했습니다.", response);
    }

@Operation(summary = "V03. 분석 결과 조회", description = "분석 ID로 하자 분석 결과를 조회합니다. COMPLETED 상태의 분석만 조회할 수 있습니다.", tags = "4. Viewer")
    @GetMapping("/{analysisId}")
    public ApiResponse<GetAnalysisResponse> getAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 분석 ID", example = "5") @PathVariable Long analysisId) {

        GetAnalysisResponse response = analysisService.getAnalysis(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "분석 결과 조회에 성공했습니다.", response);
    }
}
