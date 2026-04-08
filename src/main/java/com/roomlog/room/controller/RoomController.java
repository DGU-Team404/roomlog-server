package com.roomlog.room.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.GetRoomsResponse;
import com.roomlog.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ApiResponse<GetRoomsResponse> getRooms(@AuthenticationPrincipal LoginUser loginUser) {
        GetRoomsResponse response = roomService.getRooms(loginUser.userId());
        return ApiResponse.success(200, "방 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/{roomId}")
    public ApiResponse<GetRoomDetailResponse> getRoomDetail(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable Long roomId) {
        GetRoomDetailResponse response = roomService.getRoomDetail(loginUser.userId(), roomId);
        return ApiResponse.success(200, "방 상세 조회에 성공했습니다.", response);
    }
}
