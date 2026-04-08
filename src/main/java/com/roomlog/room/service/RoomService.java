package com.roomlog.room.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.room.domain.Room;
import com.roomlog.room.dto.GetRoomDetailResponse;
import com.roomlog.room.dto.GetRoomsResponse;
import com.roomlog.room.dto.RoomListItemResponse;
import com.roomlog.room.dto.UpdateRoomRequest;
import com.roomlog.room.dto.UpdateRoomResponse;
import com.roomlog.room.repository.RoomRepository;
import com.roomlog.scan.domain.Scan;
import com.roomlog.scan.repository.ScanRepository;
import com.roomlog.user.domain.User;
import com.roomlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetRoomsResponse getRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));

        List<Room> rooms = roomRepository.findByUserId(userId);

        List<RoomListItemResponse> roomItems = rooms.stream()
                .map(room -> {
                    Scan latestScan = scanRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);
                    return RoomListItemResponse.of(room, latestScan);
                })
                .toList();

        Room mainRoom = user.getMainRoomId() != null
                ? roomRepository.findById(user.getMainRoomId()).orElse(null)
                : null;

        return GetRoomsResponse.of(mainRoom, roomItems);
    }

    @Transactional(readOnly = true)
    public GetRoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        Scan latestScan = scanRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId)
                .orElse(null);

        return GetRoomDetailResponse.of(room, latestScan);
    }

    @Transactional
    public UpdateRoomResponse updateRoom(Long userId, Long roomId, UpdateRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        if (request.getMoveOutDate() != null && request.getMoveInDate().isAfter(request.getMoveOutDate())) {
            throw new CustomException(ErrorCode.COMMON_400, "입주일은 퇴거일보다 이후일 수 없습니다.");
        }

        room.update(request.getName(), request.getAddress(), request.getMoveInDate(), request.getMoveOutDate());

        return UpdateRoomResponse.from(room);
    }
}
