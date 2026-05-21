package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.analysis.domain.Analysis;
import lombok.Getter;

@Getter
public class GetAnalysisStatusResponse {

    @JsonProperty("analysis_id")
    private final Long analysisId;

    private final String status;

    private GetAnalysisStatusResponse(Long analysisId, String status) {
        this.analysisId = analysisId;
        this.status = status;
    }

    public static GetAnalysisStatusResponse from(Analysis analysis) {
        return new GetAnalysisStatusResponse(analysis.getId(), analysis.getStatus().name());
    }
}
