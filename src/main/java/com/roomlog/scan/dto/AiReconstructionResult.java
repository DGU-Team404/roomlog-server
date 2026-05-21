package com.roomlog.scan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiReconstructionResult {

    private boolean success;

    @JsonProperty("file_url")
    private String fileUrl;
}
