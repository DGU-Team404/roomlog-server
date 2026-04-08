package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SetMainRoomResponse {

    @JsonProperty("main_room_id")
    private final Long mainRoomId;

    private SetMainRoomResponse(Long mainRoomId) {
        this.mainRoomId = mainRoomId;
    }

    public static SetMainRoomResponse of(Long mainRoomId) {
        return new SetMainRoomResponse(mainRoomId);
    }
}
