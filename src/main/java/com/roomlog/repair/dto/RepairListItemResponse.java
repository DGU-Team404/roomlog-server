package com.roomlog.repair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.repair.domain.Repair;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RepairListItemResponse {

    @JsonProperty("repair_id")
    private final Long repairId;
    @JsonProperty("provider_name")
    private final String providerName;
    @JsonProperty("repair_cost")
    private final Integer repairCost;
    private final String status;
    @JsonProperty("repaired_at")
    private final LocalDateTime repairedAt;
    private final String note;
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private RepairListItemResponse(Long repairId, String providerName, Integer repairCost,
                                   String status, LocalDateTime repairedAt, String note, LocalDateTime createdAt) {
        this.repairId = repairId;
        this.providerName = providerName;
        this.repairCost = repairCost;
        this.status = status;
        this.repairedAt = repairedAt;
        this.note = note;
        this.createdAt = createdAt;
    }

    public static RepairListItemResponse from(Repair repair) {
        return new RepairListItemResponse(
                repair.getId(),
                repair.getProviderName(),
                repair.getRepairCost(),
                repair.getStatus().name(),
                repair.getRepairedAt(),
                repair.getNote(),
                repair.getCreatedAt()
        );
    }
}
