package com.roomlog.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class GetAnalysisCostResponse {

    @JsonProperty("analysis_id")
    private final Long analysisId;

    @JsonProperty("total_cost")
    private final int totalCost;

    @JsonProperty("cost_breakdown")
    private final List<CostBreakdownItem> costBreakdown;

    private GetAnalysisCostResponse(Long analysisId, List<Defect> defects) {
        this.analysisId = analysisId;
        this.totalCost = defects.stream().mapToInt(Defect::getEstimatedCost).sum();
        this.costBreakdown = defects.stream()
                .collect(Collectors.groupingBy(Defect::getType))
                .entrySet().stream()
                .map(CostBreakdownItem::from)
                .toList();
    }

    public static GetAnalysisCostResponse of(Long analysisId, List<Defect> defects) {
        return new GetAnalysisCostResponse(analysisId, defects);
    }

    @Getter
    public static class CostBreakdownItem {

        private final String type;
        private final int count;
        private final int cost;

        private CostBreakdownItem(String type, int count, int cost) {
            this.type = type;
            this.count = count;
            this.cost = cost;
        }

        public static CostBreakdownItem from(Map.Entry<String, List<Defect>> entry) {
            return new CostBreakdownItem(
                    entry.getKey(),
                    entry.getValue().size(),
                    entry.getValue().stream().mapToInt(Defect::getEstimatedCost).sum()
            );
        }
    }
}
