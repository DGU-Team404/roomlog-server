package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CreateRoomResponse {

    @JsonProperty("room_id")
    private final Long roomId;

    private final String name;

    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;

    @JsonProperty("ply_url")
    private final String plyUrl;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("linked_scan")
    private final LinkedScan linkedScan;

    private CreateRoomResponse(Room room, Scan scan) {
        this.roomId = room.getId();
        this.name = room.getName();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
        this.plyUrl = room.getPlyUrl();
        this.createdAt = room.getCreatedAt();
        this.linkedScan = new LinkedScan(scan);
    }

    public static CreateRoomResponse of(Room room, Scan scan) {
        return new CreateRoomResponse(room, scan);
    }

    @Getter
    public static class LinkedScan {

        @JsonProperty("scan_id")
        private final Long scanId;

        private final String status;

        private LinkedScan(Scan scan) {
            this.scanId = scan.getId();
            this.status = scan.getStatus().name();
        }
    }
}
