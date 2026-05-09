package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRoomRequest {

    @NotBlank
    private String name;

    @NotNull
    @JsonProperty("scan_id")
    private Long scanId;
}
