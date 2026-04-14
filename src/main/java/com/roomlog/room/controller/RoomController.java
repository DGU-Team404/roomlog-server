package com.roomlog.room.controller;

import com.roomlog.defect.dto.GetDefectEntryResponse;
import com.roomlog.defect.service.DefectService;
import com.roomlog.scan.dto.GetRoomScansResponse;
import com.roomlog.scan.service.ScanService;
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
    private final DefectService defectService;
    private final ScanService scanService;

    @Operation(summary = "S05. 방 생성", description = "방 이름, 주소, 입주/퇴거일과 scan_id를 입력하면 업로드된 스캔과 함께 새로운 방을 생성하고 해당 스캔을 방에 연결합니다. 각 방은 하나의 스캔만 가집니다.", tags = "4. 스캔")
    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(loginUser.userId(), request);
        return ApiResponse.success(201, "방 생성에 성공했습니다.", response);
    }

    @Operation(summary = "H01. 방 목록 조회", description = "로그인한 사용자의 대표 방(main room) 요약 정보와 전체 room 리스트를 조회합니다. 대표 방은 최초 방 생성 시 가장 마지막에 생성된 방으로 자동 설정되며, 사용자가 직접 변경한 이후에는 해당 설정이 우선 적용됩니다.")
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

    @Operation(summary = "H05. 대표 방 설정", description = "선택한 room을 사용자의 대표 방(main room)으로 설정하고 User.main_room_id를 갱신합니다.")
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

    @Operation(summary = "V02. 방의 스캔 목록 조회", description = "방 ID로 해당 방에 연결된 전체 스캔 목록(IN/OUT)을 조회합니다. 입주/퇴거 비교 대상 선택 시 사용합니다.", tags = "3. Viewer")
    @GetMapping("/{roomId}/scans")
    public ApiResponse<GetRoomScansResponse> getRoomScans(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        GetRoomScansResponse response = scanService.getRoomScans(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방의 스캔 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "D01. 하자 관리 진입 정보 조회", description = "선택한 방의 기본 정보를 조회합니다. 하자 관리 화면 진입 시 사용합니다.", tags = "6. 하자")
    @GetMapping("/{roomId}/defects")
    public ApiResponse<GetDefectEntryResponse> getDefectEntry(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        GetDefectEntryResponse response = defectService.getDefectEntry(loginUser.userId(), roomId);
        return ApiResponse.success(200, "하자 관리 진입 정보 조회에 성공했습니다.", response);
    }
}
