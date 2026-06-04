package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DeleteAnalysisResponse {

    @JsonProperty("deleted_analysis_id")
    private final Long deletedAnalysisId;

    private DeleteAnalysisResponse(Long deletedAnalysisId) {
        this.deletedAnalysisId = deletedAnalysisId;
    }

    public static DeleteAnalysisResponse of(Long deletedAnalysisId) {
        return new DeleteAnalysisResponse(deletedAnalysisId);
    }
}
