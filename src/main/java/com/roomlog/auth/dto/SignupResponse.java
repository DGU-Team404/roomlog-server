package com.roomlog.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignupResponse {

    @JsonProperty("user_id")
    private final Long userId;
    private final String email;
    private final String nickname;
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private SignupResponse(Long userId, String email, String nickname, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}
