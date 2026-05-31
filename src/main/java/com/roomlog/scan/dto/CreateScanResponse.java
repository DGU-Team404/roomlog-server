package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateScanResponse {

    @JsonProperty("scan_id")
    private final Long scanId;

    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private CreateScanResponse(Scan scan) {
        this.scanId = scan.getId();
        this.status = scan.getStatus().name();
        this.createdAt = scan.getCreatedAt();
    }

    public static CreateScanResponse from(Scan scan) {
        return new CreateScanResponse(scan);
    }
}
