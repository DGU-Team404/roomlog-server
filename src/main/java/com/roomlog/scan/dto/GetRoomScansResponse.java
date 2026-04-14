package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRoomScansResponse {

    @JsonProperty("room_id")
    private final Long roomId;

    @JsonProperty("room_name")
    private final String roomName;

    private final List<RoomScanListItemResponse> scans;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetRoomScansResponse(Room room, List<RoomScanListItemResponse> scans) {
        this.roomId = room.getId();
        this.roomName = room.getName();
        this.scans = scans;
        this.totalCount = scans.size();
    }

    public static GetRoomScansResponse of(Room room, List<Scan> scans) {
        List<RoomScanListItemResponse> scanItems = scans.stream()
                .map(RoomScanListItemResponse::from)
                .toList();
        return new GetRoomScansResponse(room, scanItems);
    }
}
