package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.dto.RegionPoint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResultRequest {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private DataPayload data;

    public List<DefectItem> getDefects() {
        return data != null ? data.getDefects() : null;
    }

    @Getter
    @NoArgsConstructor
    public static class DataPayload {
        @JsonProperty("defects")
        private List<DefectItem> defects;
    }

    @Getter
    @NoArgsConstructor
    public static class DefectItem {

        @JsonProperty("type")
        private String type;

        @JsonProperty("severity")
        private String severity;

        @JsonProperty("location")
        private String location;

        @JsonProperty("area")
        private Float area;

        @JsonProperty("description")
        private String description;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("region_3d")
        private List<RegionPoint> region3d;
    }
}
