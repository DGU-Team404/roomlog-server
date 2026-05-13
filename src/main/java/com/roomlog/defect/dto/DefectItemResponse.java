package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import lombok.Getter;

@Getter
public class DefectItemResponse {

    @JsonProperty("defect_id")
    private final Long defectId;

    @JsonProperty("analysis_id")
    private final Long analysisId;

    private final String type;

    private final String location;

    private final String severity;

    private final Float area;

    @JsonProperty("estimated_cost")
    private final Integer estimatedCost;

    private final Float x;

    private final Float y;

    private final Float z;

    private final String description;

    private DefectItemResponse(Defect defect) {
        this.defectId = defect.getId();
        this.analysisId = defect.getAnalysisId();
        this.type = defect.getType();
        this.location = defect.getLocation();
        this.severity = defect.getSeverity();
        this.area = defect.getArea();
        this.estimatedCost = defect.getEstimatedCost();
        this.x = defect.getX();
        this.y = defect.getY();
        this.z = defect.getZ();
        this.description = defect.getDescription();
    }

    public static DefectItemResponse from(Defect defect) {
        return new DefectItemResponse(defect);
    }
}
