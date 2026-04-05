package com.roomlog.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.user.domain.User;
import lombok.Getter;

@Getter
public class LoginResponse {

    @JsonProperty("user_id")
    private final Long userId;
    private final String email;
    private final String nickname;
    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("token_type")
    private final String tokenType = "Bearer";

    private LoginResponse(Long userId, String email, String nickname, String accessToken) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
    }

    public static LoginResponse of(User user, String accessToken) {
        return new LoginResponse(user.getId(), user.getEmail(), user.getNickname(), accessToken);
    }
}