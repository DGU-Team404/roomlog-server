package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.estimate.domain.Estimate;
import com.roomlog.estimate.domain.EstimateDefect;
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

    private final String message;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private final List<DefectRef> defects;

    private EstimateListItemResponse(Estimate estimate, List<EstimateDefect> estimateDefects) {
        this.estimateId = estimate.getId();
        this.providerName = estimate.getProviderName();
        this.providerPhone = estimate.getProviderPhone();
        this.providerAddress = estimate.getProviderAddress();
        this.status = estimate.getStatus().name();
        this.message = estimate.getMessage();
        this.createdAt = estimate.getCreatedAt();
        this.defects = estimateDefects.stream()
                .map(ed -> new DefectRef(ed.getDefectId()))
                .toList();
    }

    public static EstimateListItemResponse of(Estimate estimate, List<EstimateDefect> estimateDefects) {
        return new EstimateListItemResponse(estimate, estimateDefects);
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
