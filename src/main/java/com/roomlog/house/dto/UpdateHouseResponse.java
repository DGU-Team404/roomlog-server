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

    private UpdateHouseResponse(Long houseId, String name, String address) {
        this.houseId = houseId;
        this.name = name;
        this.address = address;
    }

    public static UpdateHouseResponse from(House house) {
        return new UpdateHouseResponse(house.getId(), house.getName(), house.getAddress());
    }
}
