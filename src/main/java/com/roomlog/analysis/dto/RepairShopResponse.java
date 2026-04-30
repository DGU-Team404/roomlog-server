package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.global.infra.KakaoLocalClient.KakaoPlace;
import lombok.Getter;

@Getter
public class RepairShopResponse {

    @JsonProperty("provider_external_id")
    private final String providerExternalId;

    @JsonProperty("provider_name")
    private final String providerName;

    @JsonProperty("provider_phone")
    private final String providerPhone;

    @JsonProperty("provider_address")
    private final String providerAddress;

    @JsonProperty("provider_lat")
    private final Double providerLat;

    @JsonProperty("provider_lng")
    private final Double providerLng;

    private final Double distance;

    @JsonProperty("provider_image_url")
    private final String providerImageUrl;

    private RepairShopResponse(KakaoPlace place) {
        this.providerExternalId = place.getId();
        this.providerName = place.getPlaceName();
        this.providerPhone = place.getPhone();
        this.providerAddress = place.getRoadAddressName() != null && !place.getRoadAddressName().isBlank()
                ? place.getRoadAddressName()
                : place.getAddressName();
        this.providerLat = place.getY() != null ? Double.parseDouble(place.getY()) : null;
        this.providerLng = place.getX() != null ? Double.parseDouble(place.getX()) : null;
        this.distance = place.getDistance() != null && !place.getDistance().isBlank()
                ? Double.parseDouble(place.getDistance()) / 1000.0
                : null;
        this.providerImageUrl = null;
    }

    public static RepairShopResponse from(KakaoPlace place) {
        return new RepairShopResponse(place);
    }
}
