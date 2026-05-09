package com.roomlog.repair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRepairListResponse {

    @JsonProperty("room_id")
    private final Long roomId;

    private final List<RepairListItemResponse> repairs;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetRepairListResponse(Long roomId, List<RepairListItemResponse> repairs) {
        this.roomId = roomId;
        this.repairs = repairs;
        this.totalCount = repairs.size();
    }

    public static GetRepairListResponse of(Long roomId, List<RepairListItemResponse> repairs) {
        return new GetRepairListResponse(roomId, repairs);
    }
}