package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CreateEstimateResponse {

    @JsonProperty("estimate_id")
    private final Long estimateId;

    private CreateEstimateResponse(Long estimateId) {
        this.estimateId = estimateId;
    }

    public static CreateEstimateResponse of(Long estimateId) {
        return new CreateEstimateResponse(estimateId);
    }
}
