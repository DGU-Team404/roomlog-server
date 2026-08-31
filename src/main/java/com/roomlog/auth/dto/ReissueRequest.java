package com.roomlog.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReissueRequest {

    /** 앱 구버전이 camelCase(refreshToken)로 보내는 경우가 있어 둘 다 받는다. */
    @NotBlank
    @JsonProperty("refresh_token")
    @JsonAlias("refreshToken")
    private String refreshToken;
}
