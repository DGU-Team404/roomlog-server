package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import lombok.Getter;

@Getter
public class GetDefectDetailResponse {

    @JsonProperty("defect_id")
    private final Long defectId;

    @JsonProperty("analysis_id")
    private final Long analysisId;

    private final String type;

    private final String location;

    private final Float area;

    @JsonProperty("estimated_cost")
    private final Integer estimatedCost;

    private final String severity;

    private final Float x;

    private final Float y;

    private final Float z;

    private final String description;

    @JsonProperty("before_image_url")
    private final String beforeImageUrl;

    @JsonProperty("after_image_url")
    private final String afterImageUrl;

    private GetDefectDetailResponse(Defect defect) {
        this.defectId = defect.getId();
        this.analysisId = defect.getAnalysisId();
        this.type = defect.getType();
        this.location = defect.getLocation();
        this.area = defect.getArea();
        this.estimatedCost = defect.getEstimatedCost();
        this.severity = defect.getSeverity();
        this.x = defect.getX();
        this.y = defect.getY();
        this.z = defect.getZ();
        this.description = defect.getDescription();
        this.beforeImageUrl = defect.getBeforeImageUrl();
        this.afterImageUrl = defect.getAfterImageUrl();
    }

    public static GetDefectDetailResponse from(Defect defect) {
        return new GetDefectDetailResponse(defect);
    }
}
