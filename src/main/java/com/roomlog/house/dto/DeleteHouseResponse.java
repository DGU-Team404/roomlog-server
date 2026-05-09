package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DeleteHouseResponse {

    @JsonProperty("deleted_house_id")
    private final Long deletedHouseId;

    @JsonProperty("main_house_id")
    private final Long mainHouseId;

    private DeleteHouseResponse(Long deletedHouseId, Long mainHouseId) {
        this.deletedHouseId = deletedHouseId;
        this.mainHouseId = mainHouseId;
    }

    public static DeleteHouseResponse of(Long deletedHouseId, Long mainHouseId) {
        return new DeleteHouseResponse(deletedHouseId, mainHouseId);
    }
}
