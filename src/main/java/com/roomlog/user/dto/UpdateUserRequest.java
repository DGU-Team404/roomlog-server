package com.roomlog.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateUserRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}
