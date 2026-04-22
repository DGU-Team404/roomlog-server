package com.roomlog.room.controller;

import com.roomlog.defect.dto.GetDefectEntryResponse;
import com.roomlog.defect.service.DefectService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.room.dto.CreateRoomRequest;
import com.roomlog.room.dto.CreateRoomResponse;
import com.roomlog.room.dto.DeleteRoomResponse;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.GetRoomsResponse;
import com.roomlog.room.dto.SetMainRoomResponse;
import com.roomlog.room.dto.UpdateRoomRequest;
import com.roomlog.room.dto.UpdateRoomResponse;
import com.roomlog.room.service.RoomService;
import com.roomlog.scan.dto.GetRoomScansResponse;
import com.roomlog.scan.service.ScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final ScanService scanService;

    @Operation(summary = "S05. 방 생성", description = "scan_id로 업로드된 스캔과 함께 새로운 방을 생성합니다. 생성된 방은 자동으로 대표 방으로 설정됩니다.", tags = "2. Scan")
    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(loginUser.userId(), request);
        return ApiResponse.success(201, "방 생성에 성공했습니다.", response);
    }

    @Operation(summary = "H01. 메인 대시보드 조회", description = "로그인한 사용자의 대표 방 요약 정보와 전체 방 리스트를 조회합니다.", tags = "1. Home")
    @GetMapping
    public ApiResponse<GetRoomsResponse> getRooms(@AuthenticationPrincipal LoginUser loginUser) {
        GetRoomsResponse response = roomService.getRooms(loginUser.userId());
        return ApiResponse.success(200, "방 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "H03. 방 상세 조회", description = "방 ID로 방의 상세 정보와 최신 스캔 정보를 조회합니다.", tags = "1. Home")
    @GetMapping("/{roomId}")
    public ApiResponse<GetRoomDetailResponse> getRoomDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 방 ID", example = "1") @PathVariable Long roomId) {
        GetRoomDetailResponse response = roomService.getRoomDetail(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 상세 조회에 성공했습니다.", response);
    }

    @Operation(summary = "H04. 방 정보 수정", description = "방 이름, 주소, 입주일, 퇴거일을 수정합니다.", tags = "1. Home")
    @PatchMapping("/{roomId}")
    public ApiResponse<UpdateRoomResponse> updateRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "수정할 방 ID", example = "1") @PathVariable Long roomId,
            @Valid @RequestBody UpdateRoomRequest request) {
        UpdateRoomResponse response = roomService.updateRoom(loginUser.userId(), roomId, request);
        return ApiResponse.success(200, "방 정보 수정에 성공했습니다.", response);
    }

    @Operation(summary = "H05. 대표 방 설정", description = "선택한 방을 대표 방으로 설정합니다.", tags = "1. Home")
    @PatchMapping("/{roomId}/main")
    public ApiResponse<SetMainRoomResponse> setMainRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "대표 방으로 설정할 방 ID", example = "1") @PathVariable Long roomId) {
        SetMainRoomResponse response = roomService.setMainRoom(loginUser.userId(), roomId);
        return ApiResponse.success(200, "대표 방 설정에 성공했습니다.", response);
    }

    @Operation(summary = "H06. 방 삭제", description = "방을 삭제합니다. 연결된 스캔, 분석, 하자, 견적, 수리 내역도 함께 soft delete됩니다.", tags = "1. Home")
    @DeleteMapping("/{roomId}")
    public ApiResponse<DeleteRoomResponse> deleteRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "삭제할 방 ID", example = "1") @PathVariable Long roomId) {
        DeleteRoomResponse response = roomService.deleteRoom(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 삭제에 성공했습니다.", response);
    }

    @Operation(summary = "V02. 방의 스캔 목록 조회", description = "방 ID로 해당 방에 연결된 전체 스캔 목록(IN/OUT)을 조회합니다. 입주/퇴거 비교 대상 선택 시 사용합니다.", tags = "3. Viewer")
    @GetMapping("/{roomId}/scan")
    public ApiResponse<GetRoomScansResponse> getRoomScans(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 방 ID", example = "1") @PathVariable Long roomId) {
        GetRoomScansResponse response = scanService.getRoomScans(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방의 스캔 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "D01. 방 하자 목록 조회", description = "선택한 방의 기본 정보를 조회합니다. 하자 관리 화면 진입 시 사용합니다.", tags = "4. Defect")
    @GetMapping("/{roomId}/defects")
    public ApiResponse<GetDefectEntryResponse> getDefectEntry(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 방 ID", example = "1") @PathVariable Long roomId) {
        GetDefectEntryResponse response = defectService.getDefectEntry(loginUser.userId(), roomId);
        return ApiResponse.success(200, "하자 관리 진입 정보 조회에 성공했습니다.", response);
    }
}
