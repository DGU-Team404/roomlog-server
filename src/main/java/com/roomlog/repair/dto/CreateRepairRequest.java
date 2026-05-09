package com.roomlog.repair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRepairRequest {

    @NotNull
    @JsonProperty("repair_cost")
    private Integer repairCost;

    private String note;
}
