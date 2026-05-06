package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class GetDefectEntryResponse {

    private final String name;

    private final String address;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

    @JsonProperty("move_in_date")
    private final LocalDate moveInDate;

    @JsonProperty("move_out_date")
    private final LocalDate moveOutDate;

    @JsonProperty("defect_count")
    private final long defectCount;

    private final List<DefectItemResponse> defects;

    private GetDefectEntryResponse(Room room, List<DefectItemResponse> defects) {
        this.name = room.getName();
        this.address = room.getAddress();
        this.thumbnailUrl = room.getThumbnailUrl();
        this.moveInDate = room.getMoveInDate();
        this.moveOutDate = room.getMoveOutDate();
        this.defectCount = defects.size();
        this.defects = defects;
    }

    public static GetDefectEntryResponse from(Room room, List<DefectItemResponse> defects) {
        return new GetDefectEntryResponse(room, defects);
    }
}
