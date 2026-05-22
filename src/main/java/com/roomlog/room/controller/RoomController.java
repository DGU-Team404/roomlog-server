package com.roomlog.room.controller;

import com.roomlog.analysis.dto.GetRepairShopsResponse;
import com.roomlog.analysis.service.RepairShopService;
import com.roomlog.defect.dto.GetDefectEntryResponse;
import com.roomlog.defect.service.DefectService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.room.dto.DeleteRoomResponse;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.UpdateRoomRequest;
import com.roomlog.room.dto.UpdateRoomResponse;
import com.roomlog.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final DefectService defectService;
    private final RepairShopService repairShopService;

    @Operation(summary = "RM01. 방 상세 조회", description = "방 ID로 방의 상세 정보와 최신 스캔 정보를 조회합니다.", tags = "2. Room")
    @GetMapping("/{roomId}")
    public ApiResponse<GetRoomDetailResponse> getRoomDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 방 ID", example = "1") @PathVariable Long roomId) {
        GetRoomDetailResponse response = roomService.getRoomDetail(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 상세 조회에 성공했습니다.", response);
    }

    @Operation(summary = "RM02. 방 정보 수정", description = "방 이름, 주소, 입주일, 퇴거일을 수정합니다.", tags = "2. Room")
    @PatchMapping("/{roomId}")
    public ApiResponse<UpdateRoomResponse> updateRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "수정할 방 ID", example = "1") @PathVariable Long roomId,
            @Valid @RequestBody UpdateRoomRequest request) {
        UpdateRoomResponse response = roomService.updateRoom(loginUser.userId(), roomId, request);
        return ApiResponse.success(200, "방 정보 수정에 성공했습니다.", response);
    }

    @Operation(summary = "RM03. 방 삭제", description = "방을 삭제합니다. 연결된 스캔, 분석, 하자, 견적, 수리 내역도 함께 삭제됩니다.", tags = "2. Room")
    @DeleteMapping("/{roomId}")
    public ApiResponse<DeleteRoomResponse> deleteRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "삭제할 방 ID", example = "1") @PathVariable Long roomId) {
        DeleteRoomResponse response = roomService.deleteRoom(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 삭제에 성공했습니다.", response);
    }

@Operation(summary = "V01. 방 하자 목록 조회", description = "선택한 방의 하자 목록을 조회합니다.", tags = "4. Viewer")
    @GetMapping("/{roomId}/defects")
    public ApiResponse<GetDefectEntryResponse> getDefectEntry(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 방 ID", example = "1") @PathVariable Long roomId) {
        GetDefectEntryResponse response = defectService.getDefectEntry(loginUser.userId(), roomId);
        return ApiResponse.success(200, "하자 관리 진입 정보 조회에 성공했습니다.", response);
    }

    @Operation(summary = "R01. 수리 업체 리스트 조회 (방 기반)", description = "방 ID 기준 주소를 좌표로 변환 후 카카오 지도 API로 주변 수리 업체를 조회합니다. 분석 없이 하자탐지 페이지에서 사용합니다.", tags = "5. Repair")
    @GetMapping("/{roomId}/repair-shops")
    public ApiResponse<GetRepairShopsResponse> getRepairShopsByRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "방 ID", example = "1") @PathVariable Long roomId,
            @RequestParam(required = false) String type,
            @Parameter(schema = @Schema(defaultValue = "10km")) @RequestParam(required = false, defaultValue = "10km") String radius,
            @RequestParam(required = false, defaultValue = "distance") String sort) {

        GetRepairShopsResponse response = repairShopService.getRepairShopsByRoom(loginUser.userId(), roomId, type, radius, sort);
        return ApiResponse.success(200, "수리 업체 리스트 조회에 성공했습니다.", response);
    }
}
