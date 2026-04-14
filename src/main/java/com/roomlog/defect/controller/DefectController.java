package com.roomlog.defect.controller;

import com.roomlog.defect.dto.GetDefectDetailResponse;
import com.roomlog.defect.service.DefectService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. 하자", description = "하자 목록 조회, 상세 조회 API")
@RestController
@RequestMapping("/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @Operation(summary = "V05. 하자 상세 조회", description = "하자 ID로 하자의 상세 정보를 조회합니다.", tags = "3. Viewer")
    @GetMapping("/{defectId}")
    public ApiResponse<GetDefectDetailResponse> getDefectDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 하자 ID", example = "101") @PathVariable Long defectId) {

        GetDefectDetailResponse response = defectService.getDefectDetail(defectId);
        return ApiResponse.success(200, "하자 상세 조회에 성공했습니다.", response);
    }
}
