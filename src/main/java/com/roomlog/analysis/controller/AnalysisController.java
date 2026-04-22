package com.roomlog.analysis.controller;

import com.roomlog.analysis.dto.AiResultRequest;
import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisCostResponse;
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

    @Operation(summary = "V02-1. 분석 생성", description = "입주(IN) 스캔과 퇴거(OUT) 스캔을 비교하여 하자 분석을 생성합니다. 분석은 PENDING 상태로 생성되며 AI 처리 완료 후 COMPLETED로 변경됩니다.", tags = "3. Viewer")
    @PostMapping
    public ApiResponse<CreateAnalysisResponse> createAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateAnalysisRequest request) {

        CreateAnalysisResponse response = analysisService.createAnalysis(loginUser.userId(), request);
        return ApiResponse.success(201, "하자 분석 생성에 성공했습니다.", response);
    }

    @Operation(summary = "V06. 수리비 요약 조회", description = "분석 ID로 하자 유형별 예상 수리비 요약을 조회합니다. COMPLETED 상태의 분석만 조회할 수 있습니다.", tags = "3. Viewer")
    @GetMapping("/{analysisId}/cost")
    public ApiResponse<GetAnalysisCostResponse> getAnalysisCost(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 분석 ID", example = "5") @PathVariable Long analysisId) {

        GetAnalysisCostResponse response = analysisService.getAnalysisCost(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "수리비 요약 조회에 성공했습니다.", response);
    }

    @Operation(summary = "V03. 분석 결과 조회", description = "분석 ID로 하자 분석 결과를 조회합니다. COMPLETED 상태의 분석만 조회할 수 있습니다.", tags = "3. Viewer")
    @GetMapping("/{analysisId}")
    public ApiResponse<GetAnalysisResponse> getAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 분석 ID", example = "5") @PathVariable Long analysisId) {

        GetAnalysisResponse response = analysisService.getAnalysis(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "분석 결과 조회에 성공했습니다.", response);
    }
}
