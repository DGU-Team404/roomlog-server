package com.roomlog.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.user.domain.User;
import lombok.Getter;

@Getter
public class DeleteUserResponse {

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("is_deleted")
    private final boolean isDeleted;

    private DeleteUserResponse(Long userId, boolean isDeleted) {
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public static DeleteUserResponse from(User user) {
        return new DeleteUserResponse(user.getId(), user.isDeleted());
    }
}
