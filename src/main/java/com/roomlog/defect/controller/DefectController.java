package com.roomlog.defect.controller;

import com.roomlog.defect.dto.GetSelfRepairResponse;
import com.roomlog.defect.service.SelfRepairService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/defects")
@RequiredArgsConstructor
public class DefectController {

    private final SelfRepairService selfRepairService;

    @Operation(summary = "V05. 자가 수리 안내 조회", description = """
            하자 ID로 자가 수리 가능 여부와 안내 정보를 조회합니다. 하자 상세 화면에서 사용합니다.

            [가능한 경우] description(수리 가능 사유), videos(유튜브 예시 영상 최대 2건, 검색 결과가 없으면 빈 배열), items(구매 필요 목록 + 쿠팡/G마켓 링크), total_cost(최저가 기준 예상 비용)가 내려갑니다.

            [불가능한 경우] description(수리 불가 사유)만 내려가며 videos와 items는 빈 배열, total_cost는 0입니다.

            최초 호출 시에만 GPT로 생성되며 이후에는 저장된 결과를 반환합니다. 생성에 수 초가 걸릴 수 있습니다.""", tags = "4. Viewer")
    @GetMapping("/{defectId}/self-repair")
    public ApiResponse<GetSelfRepairResponse> getSelfRepairGuide(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 하자 ID", example = "12") @PathVariable Long defectId) {

        GetSelfRepairResponse response = selfRepairService.getSelfRepairGuide(loginUser.userId(), defectId);
        return ApiResponse.success(200, "자가 수리 안내 조회에 성공했습니다.", response);
    }
}
