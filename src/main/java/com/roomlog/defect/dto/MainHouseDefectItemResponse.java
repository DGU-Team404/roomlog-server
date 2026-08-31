package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.Defect;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 챗봇에서 "등록된 하자에서 선택하기"로 띄우는 목록의 한 줄.
 * 상세 화면용 DefectItemResponse와 달리 3D 좌표·면적처럼 목록에 쓰지 않는 값은 빼고,
 * 대신 어느 방의 하자인지 보여줄 room 정보를 함께 담는다.
 */
@Getter
public class MainHouseDefectItemResponse {

    @JsonProperty("defect_id")
    private final Long defectId;

    private final String type;

    private final String severity;

    private final String location;

    @JsonProperty("room_id")
    private final Long roomId;

    @JsonProperty("room_name")
    private final String roomName;

    /** 하자 탐지 시 저장된 이미지. 없으면 null이며, 앱에서는 기본 썸네일로 대체한다. */
    @JsonProperty("image_url")
    private final String imageUrl;

    /** 하자가 등록된(= 하자를 만든 분석이 생성된) 시각. */
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private MainHouseDefectItemResponse(Defect defect, Long roomId, String roomName, LocalDateTime createdAt) {
        this.defectId = defect.getId();
        this.type = defect.getType();
        this.severity = defect.getSeverity();
        this.location = defect.getLocation();
        this.roomId = roomId;
        this.roomName = roomName;
        this.imageUrl = defect.getImageUrl();
        this.createdAt = createdAt;
    }

    public static MainHouseDefectItemResponse of(Defect defect, Long roomId, String roomName, LocalDateTime createdAt) {
        return new MainHouseDefectItemResponse(defect, roomId, roomName, createdAt);
    }
}
