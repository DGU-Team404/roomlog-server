package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.dto.RegionPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiCompareRequest {

    @JsonProperty("analysis_id")
    private Long analysisId;

    @JsonProperty("in_scan_id")
    private Long inScanId;

    @JsonProperty("in_scan_url")
    private String inScanUrl;

    @JsonProperty("in_defects_json")
    private List<DefectItem> inDefectsJson;

    @JsonProperty("out_scan_id")
    private Long outScanId;

    @JsonProperty("out_scan_url")
    private String outScanUrl;

    @JsonProperty("out_defects_json")
    private List<DefectItem> outDefectsJson;

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
