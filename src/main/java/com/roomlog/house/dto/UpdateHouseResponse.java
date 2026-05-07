package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.house.domain.House;
import lombok.Getter;

@Getter
public class UpdateHouseResponse {

    @JsonProperty("house_id")
    private final Long houseId;

    private final String name;

    private UpdateHouseResponse(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }

    public static UpdateHouseResponse from(House house) {
        return new UpdateHouseResponse(house.getId(), house.getName());
    }
}
