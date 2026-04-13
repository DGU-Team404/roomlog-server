package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetScanResponse {

    @JsonProperty("scan_id")
    private final Long scanId;

    @JsonProperty("room_id")
    private final Long roomId;

    @JsonProperty("file_url")
    private final String fileUrl;

    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private GetScanResponse(Scan scan) {
        this.scanId = scan.getId();
        this.roomId = scan.getRoomId();
        this.fileUrl = scan.getFileUrl();
        this.status = scan.getStatus().name();
        this.createdAt = scan.getCreatedAt();
    }

    public static GetScanResponse from(Scan scan) {
        return new GetScanResponse(scan);
    }
}
