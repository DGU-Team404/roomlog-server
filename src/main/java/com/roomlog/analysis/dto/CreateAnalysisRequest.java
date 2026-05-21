package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateAnalysisRequest {

    @NotNull
    @JsonProperty("in_room_id")
    private Long inRoomId;

    @JsonProperty("out_room_id")
    private Long outRoomId;
}
