package com.roomlog.user.controller;

import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import com.roomlog.user.dto.DeleteUserResponse;
import com.roomlog.user.dto.GetMyProfileResponse;
import com.roomlog.user.dto.UpdateUserRequest;
import com.roomlog.user.dto.UpdateUserResponse;
import com.roomlog.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<GetMyProfileResponse> getMyProfile(@AuthenticationPrincipal LoginUser loginUser) {
        GetMyProfileResponse response = userService.getMyProfile(loginUser.userId());
        return ApiResponse.success(200, "내 정보 조회에 성공했습니다.", response);
    }

    @PatchMapping
    public ApiResponse<UpdateUserResponse> updateUser(@AuthenticationPrincipal LoginUser loginUser,
                                                      @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUser(loginUser.userId(), request);
        return ApiResponse.success(200, "내 정보 수정에 성공했습니다.", response);
    }

    @DeleteMapping
    public ApiResponse<DeleteUserResponse> deleteUser(@AuthenticationPrincipal LoginUser loginUser) {
        DeleteUserResponse response = userService.deleteUser(loginUser.userId());
        return ApiResponse.success(200, "회원 탈퇴가 완료되었습니다.", response);
    }
}
