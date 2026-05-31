package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class RoomListItemResponse {

    @JsonProperty("room_id")
    private final Long roomId;

    private final String name;

    @JsonProperty("latest_scan")
    private final LatestScanInfo latestScan;

    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;

    @JsonProperty("ply_url")
    private final String plyUrl;

    @JsonProperty("recent_scan_date")
    private final LocalDateTime recentScanDate;

    @JsonProperty("latest_scan_status")
    private final String latestScanStatus;

    private RoomListItemResponse(Long roomId, String name, LatestScanInfo latestScan,
                                 LocalDate moveInDate, LocalDate moveOutDate, String plyUrl,
                                 LocalDateTime recentScanDate, String latestScanStatus) {
        this.roomId = roomId;
        this.name = name;
        this.latestScan = latestScan;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.plyUrl = plyUrl;
        this.recentScanDate = recentScanDate;
        this.latestScanStatus = latestScanStatus;
    }

    public static RoomListItemResponse of(Room room, Scan scan) {
        LatestScanInfo latestScanInfo = scan != null ? LatestScanInfo.from(scan) : null;
        LocalDateTime recentScanDate = scan != null ? scan.getCreatedAt() : null;
        String latestScanStatus = scan != null ? scan.getStatus().name() : null;
        return new RoomListItemResponse(
                room.getId(), room.getName(), latestScanInfo,
                room.getMoveInDate(), room.getMoveOutDate(), room.getPlyUrl(),
                recentScanDate, latestScanStatus
        );
    }

    @Getter
    public static class LatestScanInfo {

        @JsonProperty("scan_id")
        private final Long scanId;

        private LatestScanInfo(Long scanId) {
            this.scanId = scanId;
        }

        public static LatestScanInfo from(Scan scan) {
            return new LatestScanInfo(scan.getId());
        }
    }
}