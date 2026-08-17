package com.roomlog.house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.house.domain.House;
import lombok.Getter;

import java.util.List;

@Getter
public class GetHousesResponse {

    @JsonProperty("main_house")
    private final MainHouseInfo mainHouse;

    private final List<HouseListItemResponse> houses;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetHousesResponse(MainHouseInfo mainHouse, List<HouseListItemResponse> houses) {
        this.mainHouse = mainHouse;
        this.houses = houses;
        this.totalCount = houses.size();
    }

    public static GetHousesResponse of(House mainHouse, List<HouseListItemResponse> houses) {
        MainHouseInfo mainHouseInfo = mainHouse != null ? MainHouseInfo.from(mainHouse) : null;
        return new GetHousesResponse(mainHouseInfo, houses);
    }

    @Getter
    public static class MainHouseInfo {

        @JsonProperty("house_id")
        private final Long houseId;

        private final String name;

        private final String address;

        @JsonProperty("house_color")
        private final String houseColor;

        @JsonProperty("floor_color")
        private final String floorColor;

        private MainHouseInfo(Long houseId, String name, String address, String houseColor, String floorColor) {
            this.houseId = houseId;
            this.name = name;
            this.address = address;
            this.houseColor = houseColor;
            this.floorColor = floorColor;
        }

        public static MainHouseInfo from(House house) {
            return new MainHouseInfo(house.getId(), house.getName(), house.getAddress(),
                    house.getHouseColor(), house.getFloorColor());
        }
    }
}
