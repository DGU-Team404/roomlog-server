package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GetMainHouseDefectsResponse {

    @JsonProperty("defect_count")
    private final long defectCount;

    private final List<MainHouseDefectItemResponse> defects;

    private GetMainHouseDefectsResponse(List<MainHouseDefectItemResponse> defects) {
        this.defectCount = defects.size();
        this.defects = defects;
    }

    public static GetMainHouseDefectsResponse from(List<MainHouseDefectItemResponse> defects) {
        return new GetMainHouseDefectsResponse(defects);
    }
}
