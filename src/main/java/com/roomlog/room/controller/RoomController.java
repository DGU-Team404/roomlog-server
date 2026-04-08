package com.roomlog.room.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.room.dto.DeleteRoomResponse;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.GetRoomsResponse;
import com.roomlog.room.dto.SetMainRoomResponse;
import com.roomlog.room.dto.UpdateRoomRequest;
import com.roomlog.room.dto.UpdateRoomResponse;
import com.roomlog.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. 방", description = "방 목록 조회, 상세 조회, 수정, 삭제 API")
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "H01. 방 목록 조회", description = "로그인한 사용자의 방 목록과 대표 방 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<GetRoomsResponse> getRooms(@AuthenticationPrincipal LoginUser loginUser) {
        GetRoomsResponse response = roomService.getRooms(loginUser.userId());
        return ApiResponse.success(200, "방 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "H03. 방 상세 조회", description = "방 ID로 방의 상세 정보와 최신 스캔 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public ApiResponse<GetRoomDetailResponse> getRoomDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        GetRoomDetailResponse response = roomService.getRoomDetail(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 상세 조회에 성공했습니다.", response);
    }

    @Operation(summary = "H04. 방 정보 수정", description = "방 이름, 주소, 입주일, 퇴거일을 수정합니다.")
    @PatchMapping("/{roomId}")
    public ApiResponse<UpdateRoomResponse> updateRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId,
            @Valid @RequestBody UpdateRoomRequest request) {
        UpdateRoomResponse response = roomService.updateRoom(loginUser.userId(), roomId, request);
        return ApiResponse.success(200, "방 정보 수정에 성공했습니다.", response);
    }

    @Operation(summary = "H05. 대표 방 설정", description = "선택한 방을 대표 방으로 설정합니다.")
    @PatchMapping("/{roomId}/main")
    public ApiResponse<SetMainRoomResponse> setMainRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        SetMainRoomResponse response = roomService.setMainRoom(loginUser.userId(), roomId);
        return ApiResponse.success(200, "대표 방 설정에 성공했습니다.", response);
    }

    @Operation(summary = "H06. 방 삭제", description = "방을 삭제합니다. 연결된 스캔, 분석, 하자, 견적, 수리 내역도 함께 삭제됩니다. 대표 방 삭제 시 가장 최근 스캔된 방으로 자동 재설정됩니다.")
    @DeleteMapping("/{roomId}")
    public ApiResponse<DeleteRoomResponse> deleteRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        DeleteRoomResponse response = roomService.deleteRoom(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 삭제에 성공했습니다.", response);
    }
}
