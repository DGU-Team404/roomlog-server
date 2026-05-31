package com.roomlog.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.room.domain.Room;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRoomsResponse {

    @JsonProperty("main_room")
    private final MainRoomInfo mainRoom;

    private final List<RoomListItemResponse> rooms;

    @JsonProperty("total_count")
    private final int totalCount;

    private GetRoomsResponse(MainRoomInfo mainRoom, List<RoomListItemResponse> rooms) {
        this.mainRoom = mainRoom;
        this.rooms = rooms;
        this.totalCount = rooms.size();
    }

    public static GetRoomsResponse of(Room mainRoom, List<RoomListItemResponse> rooms) {
        MainRoomInfo mainRoomInfo = mainRoom != null ? MainRoomInfo.from(mainRoom) : null;
        return new GetRoomsResponse(mainRoomInfo, rooms);
    }

    @Getter
    public static class MainRoomInfo {

        @JsonProperty("room_id")
        private final Long roomId;

        private final String name;

        @JsonProperty("ply_url")
        private final String plyUrl;

        private MainRoomInfo(Long roomId, String name, String plyUrl) {
            this.roomId = roomId;
            this.name = name;
            this.plyUrl = plyUrl;
        }

        public static MainRoomInfo from(Room room) {
            return new MainRoomInfo(room.getId(), room.getName(), room.getPlyUrl());
        }
    }
}
