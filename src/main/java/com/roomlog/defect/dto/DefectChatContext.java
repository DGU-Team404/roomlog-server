package com.roomlog.defect.dto;

/**
 * 챗봇 답변을 만들 때 GPT에 실어 보낼 하자 정보.
 * selfRepairPossible은 SelfRepairPolicy가 정한 확정 판정이다.
 * 챗봇 답변과 자가 수리 안내(V05)가 서로 다른 말을 하지 않도록 같은 룰을 쓴다.
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
