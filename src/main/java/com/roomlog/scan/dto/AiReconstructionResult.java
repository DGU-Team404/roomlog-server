package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiReconstructionResult {

    @JsonProperty("scan_id")
    private Long scanId;

    @JsonProperty("scan_url")
    private String scanUrl;
}
