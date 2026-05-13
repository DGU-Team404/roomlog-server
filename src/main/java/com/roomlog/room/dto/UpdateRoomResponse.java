package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateRoomResponse {

    @JsonProperty("room_id")
    private final Long roomId;
    private final String name;
    private final String address;
    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;
    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;
    @JsonProperty("file_url")
    private final String fileUrl;

    private UpdateRoomResponse(Room room) {
        this.roomId = room.getId();
        this.name = room.getName();
        this.address = room.getAddress();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
        this.fileUrl = room.getFileUrl();
    }

    public static UpdateRoomResponse from(Room room) {
        return new UpdateRoomResponse(room);
    }
}
