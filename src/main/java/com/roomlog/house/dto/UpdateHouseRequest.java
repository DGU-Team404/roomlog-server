package com.roomlog.house.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateHouseRequest {

    @NotBlank
    private String name;
}
