package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.DefectRepairGuide;
import lombok.Getter;

import java.util.List;

@Getter
public class GetSelfRepairResponse {

    @JsonProperty("defect_id")
    private final Long defectId;

    @JsonProperty("self_repair_possible")
    private final boolean selfRepairPossible;

    private final String description;

    @JsonProperty("video_url")
    private final String videoUrl;

    private final List<RepairItem> items;

    @JsonProperty("total_cost")
    private final Integer totalCost;

    private GetSelfRepairResponse(DefectRepairGuide guide) {
        this.defectId = guide.getDefectId();
        this.selfRepairPossible = guide.isSelfRepairPossible();
        this.description = guide.getDescription();
        this.videoUrl = guide.getVideoUrl();
        this.items = guide.getItems() != null ? guide.getItems() : List.of();
        this.totalCost = guide.getTotalCost();
    }

    public static GetSelfRepairResponse from(DefectRepairGuide guide) {
        return new GetSelfRepairResponse(guide);
    }
}
