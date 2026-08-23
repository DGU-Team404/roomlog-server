package com.roomlog.defect.domain;

import java.util.Map;
import java.util.Set;

/**
 * 자가 수리 가능 여부 판정 룰.
 * GPT에 맡기면 같은 하자에 매번 다른 답이 나오므로 판정은 백엔드가 고정하고,
 * GPT는 설명문과 검색 키워드 생성만 담당한다.
 */
public final class SelfRepairPolicy {

    /** 이 면적(㎡)을 넘으면 타입과 무관하게 전문 업체 영역으로 본다. */
    private static final float MAX_SELF_REPAIR_AREA = 2.0f;

    /** 타입별로 자가 수리가 불가능해지는 심각도. 이 심각도 이상이면 불가능. */
    private static final Map<String, Set<String>> BLOCKING_SEVERITY = Map.of(
            "SCRATCH", Set.of(),                        // 긁힘: 심각도 무관하게 가능
            "STAIN", Set.of(),                          // 오염: 심각도 무관하게 가능
            "PEELING", Set.of("HIGH"),                  // 들뜸/벗겨짐: 넓으면 도배 전문
            "CRACK", Set.of("HIGH", "MEDIUM", "MID"),   // 균열: 구조 문제 가능성
            "BREAKAGE", Set.of("HIGH", "MEDIUM", "MID", "LOW") // 파손: 항상 불가능
    );

    private SelfRepairPolicy() {
    }

    public static boolean isSelfRepairable(String type, String severity, Float area) {
        if (area != null && area > MAX_SELF_REPAIR_AREA) return false;

        String normalizedType = type != null ? type.toUpperCase() : "";
        String normalizedSeverity = severity != null ? severity.toUpperCase() : "";

        Set<String> blocking = BLOCKING_SEVERITY.get(normalizedType);
        if (blocking == null) {
            // 정의되지 않은 타입은 보수적으로 HIGH만 차단
            return !"HIGH".equals(normalizedSeverity);
        }
        return !blocking.contains(normalizedSeverity);
    }
}
