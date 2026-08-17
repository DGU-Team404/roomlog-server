package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateHouseRequest {

    @NotBlank
    private String name;

    private String address;

    @JsonProperty("house_color")
    private String houseColor;

    @JsonProperty("floor_color")
    private String floorColor;
}
