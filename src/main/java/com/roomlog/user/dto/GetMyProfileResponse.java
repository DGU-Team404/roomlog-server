package com.roomlog.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetMyProfileResponse {

    @JsonProperty("user_id")
    private final Long userId;

    private final String email;

    private final String nickname;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private GetMyProfileResponse(Long userId, String email, String nickname, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public static GetMyProfileResponse from(User user) {
        return new GetMyProfileResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}
