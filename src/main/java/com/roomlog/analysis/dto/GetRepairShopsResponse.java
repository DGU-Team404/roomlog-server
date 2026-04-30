package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRepairShopsResponse {

    @JsonProperty("analysis_id")
    private final Long analysisId;

    private final String type;
    private final String radius;
    private final String sort;

    @JsonProperty("repair_shops")
    private final List<RepairShopResponse> repairShops;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetRepairShopsResponse(Long analysisId, String type, String radius, String sort, List<RepairShopResponse> repairShops) {
        this.analysisId = analysisId;
        this.type = type;
        this.radius = radius;
        this.sort = sort;
        this.repairShops = repairShops;
        this.totalCount = repairShops.size();
    }

    public static GetRepairShopsResponse of(Long analysisId, String type, String radius, String sort, List<RepairShopResponse> repairShops) {
        return new GetRepairShopsResponse(analysisId, type, radius, sort, repairShops);
    }
}
