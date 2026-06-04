package com.roomlog.analysis.controller;

import com.roomlog.analysis.dto.AiResultRequest;
import com.roomlog.analysis.dto.CreateAnalysisRequest;
import com.roomlog.analysis.dto.CreateAnalysisResponse;
import com.roomlog.analysis.dto.DeleteAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisResponse;
import com.roomlog.analysis.dto.GetAnalysisStatusResponse;
import com.roomlog.analysis.dto.GetComparisonAnalysisListResponse;
import com.roomlog.analysis.service.AnalysisService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "V04. 내방비교 분석 목록 조회", description = "집 ID 기준으로 비교 분석(두 방 비교) 내역 목록을 조회합니다. 단일 스캔 분석은 포함되지 않으며, 각 항목에 하자 목록과 요약 정보가 포함됩니다.", tags = "4. Viewer")
    @GetMapping
    public ApiResponse<List<GetComparisonAnalysisListResponse>> getComparisonAnalyses(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 집 ID", example = "1") @RequestParam Long houseId) {

        List<GetComparisonAnalysisListResponse> response = analysisService.getComparisonAnalyses(loginUser.userId(), houseId);
        return ApiResponse.success(200, "내방비교 분석 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "V04-1. 내방비교 분석 삭제", description = "분석 ID로 비교 분석 결과를 삭제합니다. 연결된 하자 목록도 함께 삭제됩니다.", tags = "4. Viewer")
    @DeleteMapping("/{analysisId}")
    public ApiResponse<DeleteAnalysisResponse> deleteAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "삭제할 분석 ID", example = "5") @PathVariable Long analysisId) {

        DeleteAnalysisResponse response = analysisService.deleteAnalysis(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "분석 결과가 삭제되었습니다.", response);
    }

    @Operation(summary = "V02-1. 분석 생성", description = """
            방을 기반으로 하자 분석을 생성합니다. 분석은 PENDING 상태로 생성되며, AI 처리 완료 후 COMPLETED로 변경됩니다. 각 방의 가장 최근 완료된 스캔이 자동으로 사용됩니다.

            [단일 방 분석] in_room_id만 전달하면 해당 방의 최신 스캔으로 하자를 탐지합니다. out_room_id는 생략하거나 null로 보내세요.

            [두 방 비교 분석] in_room_id(입주 전 방)와 out_room_id(퇴거 후 방)를 모두 전달하면 두 방의 최신 스캔을 비교하여 새로 생긴 하자를 탐지합니다.""", tags = "4. Viewer")
    @PostMapping
    public ApiResponse<CreateAnalysisResponse> createAnalysis(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateAnalysisRequest request) {

        CreateAnalysisResponse response = analysisService.createAnalysis(loginUser.userId(), request);
        return ApiResponse.success(201, "하자 분석 생성에 성공했습니다.", response);
    }

    @Operation(summary = "V02-2. 분석 상태 조회", description = "분석 ID로 현재 처리 상태를 조회합니다. 상태는 PENDING / COMPLETED / FAILED 중 하나입니다. AI 처리 완료 여부를 폴링할 때 사용합니다.", tags = "4. Viewer")
    @GetMapping("/{analysisId}/status")
    public ApiResponse<GetAnalysisStatusResponse> getAnalysisStatus(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 분석 ID", example = "5") @PathVariable Long analysisId) {

        GetAnalysisStatusResponse response = analysisService.getAnalysisStatus(loginUser.userId(), analysisId);
        return ApiResponse.success(200, "분석 상태 조회에 성공했습니다.", response);
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
