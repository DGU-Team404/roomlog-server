package com.roomlog.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReissueRequest {

    @NotBlank
    @JsonProperty("refresh_token")
    private String refreshToken;
}
