package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.house.domain.House;
import lombok.Getter;

@Getter
public class UpdateHouseResponse {

    @JsonProperty("house_id")
    private final Long houseId;

    private final String name;

    private final String address;

    @JsonProperty("house_color")
    private final String houseColor;

    @JsonProperty("floor_color")
    private final String floorColor;

    private UpdateHouseResponse(Long houseId, String name, String address, String houseColor, String floorColor) {
        this.houseId = houseId;
        this.name = name;
        this.address = address;
        this.houseColor = houseColor;
        this.floorColor = floorColor;
    }

    public static UpdateHouseResponse from(House house) {
        return new UpdateHouseResponse(house.getId(), house.getName(), house.getAddress(),
                house.getHouseColor(), house.getFloorColor());
    }
}
