package com.roomlog.defect.service;

import com.roomlog.defect.dto.GetDefectEntryResponse;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefectService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public GetDefectEntryResponse getDefectEntry(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        if (!room.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ROOM_002);
        }

        return GetDefectEntryResponse.from(room);
    }
}
