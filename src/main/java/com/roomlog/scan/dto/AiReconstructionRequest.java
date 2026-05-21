package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiReconstructionRequest {

    @JsonProperty("scan_id")
    private Long scanId;

    @JsonProperty("scan_url")
    private String scanUrl;
}
