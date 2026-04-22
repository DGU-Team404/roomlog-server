package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateAnalysisRequest {

    @NotNull
    @JsonProperty("room_id")
    private Long roomId;

    @NotNull
    @JsonProperty("in_scan_id")
    private Long inScanId;

    @NotNull
    @JsonProperty("out_scan_id")
    private Long outScanId;
}
