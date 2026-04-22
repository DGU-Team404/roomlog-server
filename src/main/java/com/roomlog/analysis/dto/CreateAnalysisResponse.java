package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.analysis.domain.Analysis;
import com.roomlog.scan.domain.Scan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateAnalysisResponse {

    @JsonProperty("analysis_id")
    private final Long analysisId;

    @JsonProperty("room_id")
    private final Long roomId;

    @JsonProperty("in_room_id")
    private final Long inRoomId;

    @JsonProperty("out_room_id")
    private final Long outRoomId;

    @JsonProperty("in_scan_id")
    private final Long inScanId;

    @JsonProperty("out_scan_id")
    private final Long outScanId;

    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private CreateAnalysisResponse(Analysis analysis, Scan inScan, Scan outScan) {
        this.analysisId = analysis.getId();
        this.roomId = analysis.getRoomId();
        this.inRoomId = inScan.getRoomId();
        this.outRoomId = outScan.getRoomId();
        this.inScanId = analysis.getInScanId();
        this.outScanId = analysis.getOutScanId();
        this.status = analysis.getStatus().name();
        this.createdAt = analysis.getCreatedAt();
    }

    public static CreateAnalysisResponse of(Analysis analysis, Scan inScan, Scan outScan) {
        return new CreateAnalysisResponse(analysis, inScan, outScan);
    }
}
