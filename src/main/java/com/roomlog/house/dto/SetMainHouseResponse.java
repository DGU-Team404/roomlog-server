package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SetMainHouseResponse {

    @JsonProperty("main_house_id")
    private final Long mainHouseId;

    private SetMainHouseResponse(Long mainHouseId) {
        this.mainHouseId = mainHouseId;
    }

    public static SetMainHouseResponse of(Long mainHouseId) {
        return new SetMainHouseResponse(mainHouseId);
    }
}
