package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateRoomRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    @JsonProperty("move_in_date")
    private LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private LocalDate moveOutDate;

    @NotNull
    @JsonProperty("scan_id")
    private Long scanId;
}
