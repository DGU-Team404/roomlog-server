package com.roomlog.estimate.controller;

import com.roomlog.estimate.dto.CreateEstimateRequest;
import com.roomlog.estimate.dto.CreateEstimateResponse;
import com.roomlog.estimate.dto.EstimatePreviewRequest;
import com.roomlog.estimate.dto.EstimatePreviewResponse;
import com.roomlog.estimate.dto.GetEstimateDetailResponse;
import com.roomlog.estimate.dto.GetEstimateListResponse;
import com.roomlog.estimate.service.EstimateService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.repair.dto.CreateRepairRequest;
import com.roomlog.repair.dto.CreateRepairResponse;
import com.roomlog.repair.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;
    private final RepairService repairService;

    @Operation(
            summary = "R03. 견적 요청 목록 조회",
            description = "사용자가 요청한 견적 목록 전체를 조회합니다. 각 견적 항목에는 업체명, 연락처, 주소, 현재 상태(REQUESTED / SENT / FAILED / COMPLETED) 목록 등이 포함됩니다.",
            tags = "5. Repair"
    )
    @GetMapping
    public ApiResponse<GetEstimateListResponse> getEstimateList(
            @AuthenticationPrincipal LoginUser loginUser) {

        GetEstimateListResponse response = estimateService.getEstimateList(loginUser.userId());
        return ApiResponse.success(200, "견적 요청 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "R04. 견적 요청 상세 조회", description = "특정 견적 요청의 상세 정보를 조회합니다.", tags = "5. Repair")
    @GetMapping("/{estimateId}")
    public ApiResponse<GetEstimateDetailResponse> getEstimateDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long estimateId) {

        GetEstimateDetailResponse response = estimateService.getEstimateDetail(loginUser.userId(), estimateId);
        return ApiResponse.success(200, "견적 요청 상세 조회에 성공했습니다.", response);
    }

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

    @Operation(
            summary = "R06. 수리 완료 등록",
            description = "견적 요청을 기준으로 수리 완료 상태를 등록하고 Repair 데이터를 생성합니다. 업체명은 견적 정보에서 자동으로 가져오며, 수리 비용과 메모를 함께 저장합니다. 연관된 하자 목록도 수리 내역에 함께 기록되고, 견적 상태는 COMPLETED로 업데이트됩니다.",
            tags = "5. Repair"
    )
    @PostMapping("/{estimateId}/repairs")
    public ApiResponse<CreateRepairResponse> registerRepair(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "수리 완료 처리할 견적 ID", example = "21") @PathVariable Long estimateId,
            @Valid @RequestBody CreateRepairRequest request) {

        CreateRepairResponse response = repairService.registerRepair(loginUser.userId(), estimateId, request);
        return ApiResponse.success(200, "수리 완료 등록에 성공했습니다.", response);
    }
}
