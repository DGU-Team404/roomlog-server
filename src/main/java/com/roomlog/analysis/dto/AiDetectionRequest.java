package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiDetectionRequest {

    @JsonProperty("analysis_id")
    private Long analysisId;

    @JsonProperty("scan_id")
    private Long scanId;

    @JsonProperty("scan_url")
    private String scanUrl;

    @JsonProperty("callback_url")
    private String callbackUrl;
}
