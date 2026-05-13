package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateEstimateRequest {

    @NotNull
    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("analysis_id")
    private Long analysisId;

    @NotEmpty
    @JsonProperty("defect_ids")
    private List<Long> defectIds;

    @NotBlank
    @JsonProperty("provider_name")
    private String providerName;

    @JsonProperty("provider_phone")
    private String providerPhone;

    @JsonProperty("provider_address")
    private String providerAddress;

    private String message;
}
