package com.roomlog.auth.controller;

import com.roomlog.auth.dto.LoginRequest;
import com.roomlog.auth.dto.LoginResponse;
import com.roomlog.auth.dto.ReissueRequest;
import com.roomlog.auth.dto.ReissueResponse;
import com.roomlog.auth.dto.SignupRequest;
import com.roomlog.auth.dto.SignupResponse;
import com.roomlog.auth.service.AuthService;
import com.roomlog.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "A01. 회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입합니다.", tags = "6. Auth")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(201, "회원가입에 성공했습니다.", response);
    }

    @Operation(summary = "A02. 로그인", description = "이메일, 비밀번호로 로그인하고 액세스 토큰을 발급받습니다.", tags = "6. Auth")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(200, "로그인에 성공했습니다.", response);
    }

    @Operation(summary = "A03. 토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.", tags = "6. Auth")
    @PostMapping("/refresh")
    public ApiResponse<ReissueResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        ReissueResponse response = authService.reissue(request);
        return ApiResponse.success(200, "토큰 재발급에 성공했습니다.", response);
    }
}
