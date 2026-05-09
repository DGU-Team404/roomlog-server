package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.house.domain.House;
import lombok.Getter;

@Getter
public class HouseListItemResponse {

    @JsonProperty("house_id")
    private final Long houseId;

    private final String name;

    private HouseListItemResponse(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }

    public static HouseListItemResponse from(House house) {
        return new HouseListItemResponse(house.getId(), house.getName());
    }
}
