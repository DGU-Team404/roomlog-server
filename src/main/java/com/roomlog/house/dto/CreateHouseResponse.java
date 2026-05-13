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

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private CreateHouseResponse(Long houseId, String name, String address, LocalDateTime createdAt) {
        this.houseId = houseId;
        this.name = name;
        this.address = address;
        this.createdAt = createdAt;
    }

    public static CreateHouseResponse from(House house) {
        return new CreateHouseResponse(house.getId(), house.getName(), house.getAddress(), house.getCreatedAt());
    }
}
