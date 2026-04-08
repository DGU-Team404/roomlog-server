package com.roomlog.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReissueResponse {

    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("token_type")
    private final String tokenType = "Bearer";

    private ReissueResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static ReissueResponse of(String accessToken) {
        return new ReissueResponse(accessToken);
    }
}
