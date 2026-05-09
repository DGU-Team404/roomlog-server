package com.roomlog.repair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.repair.domain.Repair;
import com.roomlog.repair.domain.RepairDefect;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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

    private final List<DefectRef> defects;

    private RepairListItemResponse(Repair repair, List<RepairDefect> repairDefects) {
        this.repairId = repair.getId();
        this.providerName = repair.getProviderName();
        this.repairCost = repair.getRepairCost();
        this.status = repair.getStatus().name();
        this.repairedAt = repair.getRepairedAt();
        this.defects = repairDefects.stream()
                .map(rd -> new DefectRef(rd.getDefectId()))
                .toList();
    }

    public static RepairListItemResponse of(Repair repair, List<RepairDefect> repairDefects) {
        return new RepairListItemResponse(repair, repairDefects);
    }

    @Getter
    public static class DefectRef {
        @JsonProperty("defect_id")
        private final Long defectId;

        public DefectRef(Long defectId) {
            this.defectId = defectId;
        }
    }
}
