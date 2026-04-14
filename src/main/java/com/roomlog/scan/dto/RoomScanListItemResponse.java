package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomScanListItemResponse {

    @JsonProperty("scan_id")
    private final Long scanId;

    @JsonProperty("scan_type")
    private final String scanType;

    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

    private RoomScanListItemResponse(Scan scan) {
        this.scanId = scan.getId();
        this.scanType = scan.getScanType().name();
        this.status = scan.getStatus().name();
        this.createdAt = scan.getCreatedAt();
        this.thumbnailUrl = scan.getThumbnailUrl();
    }

    public static RoomScanListItemResponse from(Scan scan) {
        return new RoomScanListItemResponse(scan);
    }
}
