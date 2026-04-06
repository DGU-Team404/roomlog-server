package com.roomlog.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.user.domain.User;
import lombok.Getter;

@Getter
public class UpdateUserResponse {

    @JsonProperty("user_id")
    private final Long userId;

    private final String nickname;

    private UpdateUserResponse(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public static UpdateUserResponse from(User user) {
        return new UpdateUserResponse(user.getId(), user.getNickname());
    }
}
