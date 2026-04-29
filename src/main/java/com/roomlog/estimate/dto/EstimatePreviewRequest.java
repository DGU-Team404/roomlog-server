package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EstimatePreviewRequest {

    @NotNull
    @JsonProperty("analysis_id")
    private Long analysisId;

    @NotBlank
    @JsonProperty("provider_external_id")
    private String providerExternalId;

    private String message;
}
