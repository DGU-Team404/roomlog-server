package com.roomlog.repair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.repair.domain.Repair;
import lombok.Getter;

@Getter
public class CreateRepairResponse {

    @JsonProperty("repair_id")
    private final Long repairId;

    @JsonProperty("estimate_id")
    private final Long estimateId;

    private final String status;

    private CreateRepairResponse(Long repairId, Long estimateId, String status) {
        this.repairId = repairId;
        this.estimateId = estimateId;
        this.status = status;
    }

    public static CreateRepairResponse of(Repair repair) {
        return new CreateRepairResponse(repair.getId(), repair.getEstimateId(), repair.getStatus().name());
    }
}
