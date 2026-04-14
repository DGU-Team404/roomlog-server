package com.roomlog.analysis.controller;

import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisResponse;
import com.roomlog.analysis.service.AnalysisService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. 분석", description = "하자 분석 생성, 결과 조회 API")
@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "V02-1. 분석 생성", description = "입주(IN) 스캔과 퇴거(OUT) 스캔을 비교하여 하자 분석을 생성합니다. 분석은 PENDING 상태로 생성되며 AI 처리 완료 후 COMPLETED로 변경됩니다.")
    @PostMapping
    public ApiResponse<CreateAnalysisResponse> createAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateAnalysisRequest request) {

        CreateAnalysisResponse response = analysisService.createAnalysis(loginUser.userId(), request);
        return ApiResponse.success(201, "하자 분석 생성에 성공했습니다.", response);
    }

    @Operation(summary = "V03. 분석 결과 조회", description = "분석 ID로 하자 분석 결과를 조회합니다. COMPLETED 상태의 분석만 조회할 수 있습니다.")
    @GetMapping("/{analysisId}")
    public ApiResponse<GetAnalysisResponse> getAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 분석 ID", example = "5") @PathVariable Long analysisId) {

        GetAnalysisResponse response = analysisService.getAnalysis(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "분석 결과 조회에 성공했습니다.", response);
    }
}
