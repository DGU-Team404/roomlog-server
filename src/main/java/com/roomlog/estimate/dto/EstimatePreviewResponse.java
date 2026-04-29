package com.roomlog.estimate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import com.roomlog.global.infra.KakaoLocalClient.KakaoPlace;
import lombok.Getter;

import java.util.List;

@Getter
public class EstimatePreviewResponse {

    @JsonProperty("analysis_id")
    private final Long analysisId;

    @JsonProperty("room_id")
    private final Long roomId;

    private final ProviderInfo provider;
    private final List<DefectSummary> defects;
    private final Summary summary;

    @JsonProperty("message_preview")
    private final String messagePreview;

    private EstimatePreviewResponse(Long analysisId, Long roomId, ProviderInfo provider,
                                    List<DefectSummary> defects, String messagePreview) {
        this.analysisId = analysisId;
        this.roomId = roomId;
        this.provider = provider;
        this.defects = defects;
        this.summary = Summary.from(defects);
        this.messagePreview = messagePreview;
    }

    public static EstimatePreviewResponse of(Long analysisId, Long roomId, KakaoPlace place,
                                             List<Defect> defects, String userMessage) {
        ProviderInfo provider = ProviderInfo.from(place);
        List<DefectSummary> defectSummaries = defects.stream().map(DefectSummary::from).toList();
        int totalCost = defects.stream().mapToInt(Defect::getEstimatedCost).sum();
        String messagePreview = buildMessagePreview(defects.size(), totalCost, userMessage);
        return new EstimatePreviewResponse(analysisId, roomId, provider, defectSummaries, messagePreview);
    }

    private static String buildMessagePreview(int defectCount, int totalCost, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요. RoomLog를 통해 문의드립니다.\n");
        sb.append(String.format("현재 총 %d건의 하자가 확인되었으며 예상 수리비는 약 %,d원입니다.\n", defectCount, totalCost));
        if (userMessage != null && !userMessage.isBlank()) {
            sb.append(userMessage);
        }
        return sb.toString();
    }

    @Getter
    public static class ProviderInfo {

        @JsonProperty("provider_external_id")
        private final String providerExternalId;

        @JsonProperty("provider_name")
        private final String providerName;

        @JsonProperty("provider_phone")
        private final String providerPhone;

        @JsonProperty("provider_address")
        private final String providerAddress;

        private ProviderInfo(KakaoPlace place) {
            this.providerExternalId = place.getId();
            this.providerName = place.getPlaceName();
            this.providerPhone = place.getPhone();
            this.providerAddress = place.getRoadAddressName() != null && !place.getRoadAddressName().isBlank()
                    ? place.getRoadAddressName()
                    : place.getAddressName();
        }

        public static ProviderInfo from(KakaoPlace place) {
            return new ProviderInfo(place);
        }
    }

    @Getter
    public static class DefectSummary {

        @JsonProperty("defect_id")
        private final Long defectId;

        private final String type;
        private final String location;
        private final String severity;

        @JsonProperty("estimated_cost")
        private final Integer estimatedCost;

        private DefectSummary(Defect defect) {
            this.defectId = defect.getId();
            this.type = defect.getType();
            this.location = defect.getLocation();
            this.severity = defect.getSeverity();
            this.estimatedCost = defect.getEstimatedCost();
        }

        public static DefectSummary from(Defect defect) {
            return new DefectSummary(defect);
        }
    }

    @Getter
    public static class Summary {

        @JsonProperty("defect_count")
        private final int defectCount;

        @JsonProperty("total_cost")
        private final int totalCost;

        private Summary(List<DefectSummary> defects) {
            this.defectCount = defects.size();
            this.totalCost = defects.stream().mapToInt(DefectSummary::getEstimatedCost).sum();
        }

        public static Summary from(List<DefectSummary> defects) {
            return new Summary(defects);
        }
    }
}
