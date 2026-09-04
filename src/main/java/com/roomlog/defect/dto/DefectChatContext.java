package com.roomlog.defect.dto;

/**
 * 챗봇 답변을 만들 때 GPT에 실어 보낼 하자 정보.
 * selfRepairPossible은 자가 수리 안내(V05)를 만들 때 모델이 내려 저장해 둔 판정을 그대로 가져온 값이다.
 * 챗봇 답변과 자가 수리 안내가 서로 다른 말을 하지 않도록 판정을 다시 만들지 않고 저장된 것을 쓴다.
 */
public record DefectChatContext(
        Long defectId,
        String type,
        String severity,
        String location,
        Float area,
        String description,
        String roomName,
        boolean selfRepairPossible) {
}
