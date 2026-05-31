package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomScanListItemResponse {

    @JsonProperty("scan_id")
    private final Long scanId;

    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("ply_url")
    private final String plyUrl;

    private RoomScanListItemResponse(Scan scan) {
        this.scanId = scan.getId();
        this.status = scan.getStatus().name();
        this.createdAt = scan.getCreatedAt();
        this.plyUrl = scan.getPlyUrl();
    }

    public static RoomScanListItemResponse from(Scan scan) {
        return new RoomScanListItemResponse(scan);
    }
}
