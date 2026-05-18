package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.dto.DefectItemResponse;
import com.roomlog.defect.domain.Defect;
import com.roomlog.estimate.domain.Estimate;
import com.roomlog.repair.domain.Repair;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EstimateListItemResponse {

    @JsonProperty("estimate_id")
    private final Long estimateId;

    @JsonProperty("provider_name")
    private final String providerName;

    @JsonProperty("provider_phone")
    private final String providerPhone;

    @JsonProperty("provider_address")
    private final String providerAddress;

    private final String status;

    @JsonProperty("display_status")
    private final String displayStatus;

    private final String message;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private final List<DefectItemResponse> defects;

    @JsonProperty("repair_id")
    private final Long repairId;

    @JsonProperty("repair_cost")
    private final Integer repairCost;

    @JsonProperty("repaired_at")
    private final LocalDateTime repairedAt;

    private EstimateListItemResponse(Estimate estimate, List<Defect> defects, Repair repair) {
        this.estimateId = estimate.getId();
        this.providerName = estimate.getProviderName();
        this.providerPhone = estimate.getProviderPhone();
        this.providerAddress = estimate.getProviderAddress();
        this.status = estimate.getStatus().name();
        this.displayStatus = estimate.getStatus() == Estimate.Status.COMPLETED ? "COMPLETED" : "IN_PROGRESS";
        this.message = estimate.getMessage();
        this.createdAt = estimate.getCreatedAt();
        this.defects = defects.stream().map(DefectItemResponse::from).toList();
        this.repairId = repair != null ? repair.getId() : null;
        this.repairCost = repair != null ? repair.getRepairCost() : null;
        this.repairedAt = repair != null ? repair.getRepairedAt() : null;
    }

    public static EstimateListItemResponse of(Estimate estimate, List<Defect> defects, Repair repair) {
        return new EstimateListItemResponse(estimate, defects, repair);
    }
}
