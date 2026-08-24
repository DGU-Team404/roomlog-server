package com.roomlog.defect.service;

import com.roomlog.analysis.repository.AnalysisRepository;
import com.roomlog.defect.domain.Defect;
import com.roomlog.defect.dto.DefectItemResponse;
import com.roomlog.defect.dto.GetDefectEntryResponse;
import com.roomlog.defect.repository.DefectRepository;
import com.roomlog.defect.service.SelfRepairService;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.house.repository.HouseRepository;
import com.roomlog.room.domain.Room;
import com.roomlog.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefectService {

    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final DefectRepository defectRepository;
    private final AnalysisRepository analysisRepository;
    private final SelfRepairService selfRepairService;

    @Transactional(readOnly = true)
    public GetDefectEntryResponse getDefectEntry(Long userId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_001));

        houseRepository.findByIdAndUserId(room.getHouseId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_002));

        List<Long> analysisIds = analysisRepository.findByRoomId(roomId).stream()
                .map(a -> a.getId())
                .toList();

        List<Defect> defects = analysisIds.isEmpty()
                ? List.of()
                : defectRepository.findByAnalysisIdIn(analysisIds);

        // 사용자가 하자를 눌러 상세를 열기 전에 자가 수리 안내를 미리 만들어둔다(백그라운드, 응답을 막지 않음).
        defects.forEach(selfRepairService::prefetchGuide);

        return GetDefectEntryResponse.from(room, defects.stream().map(DefectItemResponse::from).toList());
    }

}
