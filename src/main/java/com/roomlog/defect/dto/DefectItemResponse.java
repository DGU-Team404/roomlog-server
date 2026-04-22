package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import lombok.Getter;

@Getter
public class DefectItemResponse {

    @JsonProperty("defect_id")
    private final Long defectId;

    private final String type;

    private final String location;

    private final String severity;

    private final Float area;

    @JsonProperty("estimated_cost")
    private final Integer estimatedCost;

    private final Float x;

    private final Float y;

    private final Float z;

    @JsonProperty("before_image_url")
    private final String beforeImageUrl;

    @JsonProperty("after_image_url")
    private final String afterImageUrl;

    private DefectItemResponse(Defect defect) {
        this.defectId = defect.getId();
        this.type = defect.getType();
        this.location = defect.getLocation();
        this.severity = defect.getSeverity();
        this.area = defect.getArea();
        this.estimatedCost = defect.getEstimatedCost();
        this.x = defect.getX();
        this.y = defect.getY();
        this.z = defect.getZ();
        this.beforeImageUrl = defect.getBeforeImageUrl();
        this.afterImageUrl = defect.getAfterImageUrl();
    }

    public static DefectItemResponse from(Defect defect) {
        return new DefectItemResponse(defect);
    }
}
