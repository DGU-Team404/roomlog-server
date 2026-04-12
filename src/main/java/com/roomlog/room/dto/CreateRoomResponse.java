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
    private final String address;

    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("linked_scan")
    private final LinkedScan linkedScan;

    private CreateRoomResponse(Room room, Scan scan) {
        this.roomId = room.getId();
        this.name = room.getName();
        this.address = room.getAddress();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
        this.thumbnailUrl = room.getThumbnailUrl();
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

        @JsonProperty("scan_type")
        private final String scanType;

        private final String status;

        private LinkedScan(Scan scan) {
            this.scanId = scan.getId();
            this.scanType = scan.getScanType().name();
            this.status = scan.getStatus().name();
        }
    }
}
