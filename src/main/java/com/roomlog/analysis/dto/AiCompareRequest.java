package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.dto.RegionPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class AiCompareRequest {

    @Getter
    @AllArgsConstructor
    public static class DefectItem {
        private String type;
        private String severity;
        private String location;
        private Float area;
        private String description;

        @JsonProperty("region_3d")
        private List<RegionPoint> region3d;
    }
}
