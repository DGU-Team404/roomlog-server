package com.roomlog.house.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateHouseRequest {

    @NotBlank
    private String name;

    private String address;
}
