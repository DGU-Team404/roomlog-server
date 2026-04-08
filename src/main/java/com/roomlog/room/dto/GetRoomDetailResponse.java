package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GetRoomDetailResponse {

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
    @JsonProperty("file_url")
    private final String fileUrl;
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
    @JsonProperty("latest_scan")
    private final LatestScanResponse latestScan;

    private GetRoomDetailResponse(Room room, Scan latestScan) {
        this.roomId = room.getId();
        this.name = room.getName();
        this.address = room.getAddress();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
        this.thumbnailUrl = room.getThumbnailUrl();
        this.fileUrl = latestScan != null ? latestScan.getFileUrl() : null;
        this.createdAt = room.getCreatedAt();
        this.latestScan = latestScan != null ? new LatestScanResponse(latestScan) : null;
    }

    public static GetRoomDetailResponse of(Room room, Scan latestScan) {
        return new GetRoomDetailResponse(room, latestScan);
    }

    @Getter
    public static class LatestScanResponse {

        @JsonProperty("scan_id")
        private final Long scanId;
        private final String status;
        @JsonProperty("created_at")
        private final LocalDateTime createdAt;

        private LatestScanResponse(Scan scan) {
            this.scanId = scan.getId();
            this.status = scan.getStatus().name();
            this.createdAt = scan.getCreatedAt();
        }
    }
}
