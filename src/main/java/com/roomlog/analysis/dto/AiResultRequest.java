package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AiResultRequest {

    private boolean success;
    private List<DefectItem> defects;

    @Getter
    @NoArgsConstructor
    public static class DefectItem {

        private String type;
        private String severity;
        private String location;
        private Float area;
        private String description;

        @JsonProperty("before_image_url")
        private String beforeImageUrl;

        @JsonProperty("after_image_url")
        private String afterImageUrl;

        private Float x;
        private Float y;
        private Float z;
    }
}
