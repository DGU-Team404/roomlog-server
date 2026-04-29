package com.roomlog.estimate.controller;

import com.roomlog.estimate.dto.CreateEstimateRequest;
import com.roomlog.estimate.dto.CreateEstimateResponse;
import com.roomlog.estimate.dto.EstimatePreviewRequest;
import com.roomlog.estimate.dto.EstimatePreviewResponse;
import com.roomlog.estimate.service.EstimateService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estimates")
@RequiredArgsConstructor
@Tag(name = "5. Repair")
public class EstimateController {

    private final EstimateService estimateService;

    @Operation(summary = "R02. 견적 요청 미리보기", description = "선택한 업체와 분석 결과 하자 정보를 기반으로 문의 내용 및 요약 정보를 미리 조회합니다. 실제 견적 요청 전 확인 화면에 사용합니다.", tags = "5. Repair")
    @PostMapping("/preview")
    public ApiResponse<EstimatePreviewResponse> previewEstimate(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody EstimatePreviewRequest request) {

        EstimatePreviewResponse response = estimateService.previewEstimate(loginUser.userId(), request);
        return ApiResponse.success(200, "견적 요청 미리보기에 성공했습니다.", response);
    }

    @Operation(summary = "R02-1. 견적 요청", description = "선택한 하자와 업체 정보를 기반으로 문의 문구를 생성하고 견적 요청 이력을 저장합니다.", tags = "5. Repair")
    @PostMapping
    public ApiResponse<CreateEstimateResponse> createEstimate(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateEstimateRequest request) {

        CreateEstimateResponse response = estimateService.createEstimate(loginUser.userId(), request);
        return ApiResponse.success(201, "견적 요청이 완료되었습니다.", response);
    }
}
