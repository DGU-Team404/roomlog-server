package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DeleteRoomResponse {

    @JsonProperty("deleted_room_id")
    private final Long deletedRoomId;
    @JsonProperty("main_room_id")
    private final Long mainRoomId;

    private DeleteRoomResponse(Long deletedRoomId, Long mainRoomId) {
        this.deletedRoomId = deletedRoomId;
        this.mainRoomId = mainRoomId;
    }

    public static DeleteRoomResponse of(Long deletedRoomId, Long mainRoomId) {
        return new DeleteRoomResponse(deletedRoomId, mainRoomId);
    }
}
