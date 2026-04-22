package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetDefectEntryResponse {

    @JsonProperty("room_id")
    private final Long roomId;

    private final String name;

    private final String address;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;

    private GetDefectEntryResponse(Room room) {
        this.roomId = room.getId();
        this.name = room.getName();
        this.address = room.getAddress();
        this.thumbnailUrl = room.getThumbnailUrl();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
    }

    public static GetDefectEntryResponse from(Room room) {
        return new GetDefectEntryResponse(room);
    }
}
