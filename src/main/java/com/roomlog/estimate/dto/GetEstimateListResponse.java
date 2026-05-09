package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GetEstimateListResponse {

    private final List<EstimateListItemResponse> estimates;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetEstimateListResponse(List<EstimateListItemResponse> estimates) {
        this.estimates = estimates;
        this.totalCount = estimates.size();
    }

    public static GetEstimateListResponse of(List<EstimateListItemResponse> estimates) {
        return new GetEstimateListResponse(estimates);
    }
}