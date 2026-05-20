package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetHouseRoomsResponse {

    @JsonProperty("house_id")
    private final Long houseId;

    @JsonProperty("house_name")
    private final String houseName;

    private final List<RoomItem> rooms;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetHouseRoomsResponse(Long houseId, String houseName, List<RoomItem> rooms) {
        this.houseId = houseId;
        this.houseName = houseName;
        this.rooms = rooms;
        this.totalCount = rooms.size();
    }

    public static GetHouseRoomsResponse of(Long houseId, String houseName, List<RoomItem> rooms) {
        return new GetHouseRoomsResponse(houseId, houseName, rooms);
    }

    @Getter
    public static class RoomItem {

        @JsonProperty("room_id")
        private final Long roomId;

        private final String name;

        @JsonProperty("file_url")
        private final String fileUrl;

        @JsonProperty("latest_scan")
        private final LatestScanInfo latestScan;

        @JsonProperty("recent_scan_date")
        private final LocalDateTime recentScanDate;

        @JsonProperty("latest_scan_status")
        private final String latestScanStatus;

        private RoomItem(Long roomId, String name, String fileUrl,
                         LatestScanInfo latestScan, LocalDateTime recentScanDate, String latestScanStatus) {
            this.roomId = roomId;
            this.name = name;
            this.fileUrl = fileUrl;
            this.latestScan = latestScan;
            this.recentScanDate = recentScanDate;
            this.latestScanStatus = latestScanStatus;
        }

        public static RoomItem of(Room room, Scan scan) {
            LatestScanInfo latestScanInfo = scan != null ? LatestScanInfo.from(scan) : null;
            LocalDateTime recentScanDate = scan != null ? scan.getCreatedAt() : null;
            String latestScanStatus = scan != null ? scan.getStatus().name() : null;
            return new RoomItem(room.getId(), room.getName(), room.getFileUrl(),
                    latestScanInfo, recentScanDate, latestScanStatus);
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
}
