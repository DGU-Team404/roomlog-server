package com.roomlog.house.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.house.dto.CreateHouseRequest;
import com.roomlog.house.dto.CreateHouseResponse;
import com.roomlog.house.dto.DeleteHouseResponse;
import com.roomlog.house.dto.GetHouseRoomsResponse;
import com.roomlog.house.dto.GetHousesResponse;
import com.roomlog.house.dto.SetMainHouseResponse;
import com.roomlog.house.dto.UpdateHouseRequest;
import com.roomlog.house.dto.UpdateHouseResponse;
import com.roomlog.house.service.HouseService;
import com.roomlog.room.dto.CreateRoomRequest;
import com.roomlog.room.dto.CreateRoomResponse;
import com.roomlog.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final RoomService roomService;

    @Operation(summary = "H00. 집 생성", description = "새로운 집을 생성합니다. 첫 번째 집은 자동으로 대표 집으로 설정됩니다.", tags = "1. House")
    @PostMapping
    public ApiResponse<CreateHouseResponse> createHouse(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CreateHouseRequest request) {
        CreateHouseResponse response = houseService.createHouse(loginUser.userId(), request);
        return ApiResponse.success(201, "집 생성에 성공했습니다.", response);
    }

    @Operation(summary = "H01. 집 목록 조회", description = "사용자의 집 목록과 대표 집 정보를 조회합니다.", tags = "1. House")
    @GetMapping
    public ApiResponse<GetHousesResponse> getHouses(@AuthenticationPrincipal LoginUser loginUser) {
        GetHousesResponse response = houseService.getHouses(loginUser.userId());
        return ApiResponse.success(200, "집 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "H02. 대표 집 설정", description = "선택한 집을 대표 집으로 설정합니다.", tags = "1. House")
    @PatchMapping("/{houseId}/main")
    public ApiResponse<SetMainHouseResponse> setMainHouse(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "대표 집으로 설정할 집 ID", example = "1") @PathVariable Long houseId) {
        SetMainHouseResponse response = houseService.setMainHouse(loginUser.userId(), houseId);
        return ApiResponse.success(200, "대표 집 설정에 성공했습니다.", response);
    }

    @Operation(summary = "H03. 집 이름 수정", description = "집 이름을 수정합니다.", tags = "1. House")
    @PatchMapping("/{houseId}")
    public ApiResponse<UpdateHouseResponse> updateHouse(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "수정할 집 ID", example = "1") @PathVariable Long houseId,
            @Valid @RequestBody UpdateHouseRequest request) {
        UpdateHouseResponse response = houseService.updateHouse(loginUser.userId(), houseId, request);
        return ApiResponse.success(200, "집 이름 수정에 성공했습니다.", response);
    }

    @Operation(summary = "H04. 집 삭제", description = "집을 삭제합니다. 하위 방, 스캔, 분석, 하자, 견적, 수리 내역이 모두 함께 삭제됩니다.", tags = "1. House")
    @DeleteMapping("/{houseId}")
    public ApiResponse<DeleteHouseResponse> deleteHouse(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "삭제할 집 ID", example = "1") @PathVariable Long houseId) {
        DeleteHouseResponse response = houseService.deleteHouse(loginUser.userId(), houseId);
        return ApiResponse.success(200, "집 삭제에 성공했습니다.", response);
    }

    @Operation(summary = "H05. 집 내 방 목록 조회", description = "특정 집에 속한 방 목록을 조회합니다.", tags = "1. House")
    @GetMapping("/{houseId}/rooms")
    public ApiResponse<GetHouseRoomsResponse> getHouseRooms(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "조회할 집 ID", example = "1") @PathVariable Long houseId) {
        GetHouseRoomsResponse response = houseService.getHouseRooms(loginUser.userId(), houseId);
        return ApiResponse.success(200, "집 내 방 목록 조회에 성공했습니다.", response);
    }

    @Operation(summary = "RM00. 방 생성", description = "특정 집에 새로운 방을 생성합니다.", tags = "2. Room")
    @PostMapping("/{houseId}/rooms")
    public ApiResponse<CreateRoomResponse> createRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "방을 추가할 집 ID", example = "1") @PathVariable Long houseId,
            @Valid @RequestBody CreateRoomRequest request) {
        CreateRoomResponse response = roomService.createRoom(loginUser.userId(), houseId, request);
        return ApiResponse.success(201, "방 생성에 성공했습니다.", response);
    }
}