package com.roomlog.analysis.controller;

import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.service.AnalysisService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. 분석", description = "분석 생성, 결과 조회, 수리비 요약 API")
@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(
            summary = "V02-1. 분석 생성",
            description = """
                    입주(IN) 스캔과 퇴거(OUT) 스캔을 비교하여 하자 분석을 생성합니다.

                    - in_scan_id는 scanType이 IN인 스캔, out_scan_id는 OUT인 스캔이어야 합니다.
                    - 두 스캔 모두 COMPLETED 상태여야 합니다.
                    - 스캔 조합이 올바르지 않으면 400(ANALYSIS_003) 에러가 반환됩니다.
                    - 분석은 PENDING 상태로 생성되며, 이후 AI 처리를 통해 결과가 채워집니다.
                    """
    )
    @PostMapping
    public ApiResponse<CreateAnalysisResponse> createAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateAnalysisRequest request) {

        CreateAnalysisResponse response = analysisService.createAnalysis(loginUser.userId(), request);
        return ApiResponse.success(201, "하자 분석 생성에 성공했습니다.", response);
    }
}
