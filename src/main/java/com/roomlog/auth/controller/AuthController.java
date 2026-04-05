package com.roomlog.auth.controller;

import com.roomlog.auth.dto.LoginRequest;
import com.roomlog.auth.dto.LoginResponse;
import com.roomlog.auth.dto.SignupRequest;
import com.roomlog.auth.dto.SignupResponse;
import com.roomlog.auth.service.AuthService;
import com.roomlog.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(201, "회원가입에 성공했습니다.", response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(200, "로그인에 성공했습니다.", response);
    }
}
