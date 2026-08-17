package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.house.domain.House;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateHouseResponse {

    @JsonProperty("house_id")
    private final Long houseId;

    private final String name;

    private final String address;

    @JsonProperty("house_color")
    private final String houseColor;

    @JsonProperty("floor_color")
    private final String floorColor;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private CreateHouseResponse(Long houseId, String name, String address, String houseColor, String floorColor,
                                LocalDateTime createdAt) {
        this.houseId = houseId;
        this.name = name;
        this.address = address;
        this.houseColor = houseColor;
        this.floorColor = floorColor;
        this.createdAt = createdAt;
    }

    public static CreateHouseResponse from(House house) {
        return new CreateHouseResponse(house.getId(), house.getName(), house.getAddress(),
                house.getHouseColor(), house.getFloorColor(), house.getCreatedAt());
    }
}
