package com.roomlog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    COMMON_400(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    COMMON_401(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    COMMON_403(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    COMMON_404(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 데이터를 찾을 수 없습니다."),
    COMMON_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    // Auth
    AUTH_001(HttpStatus.BAD_REQUEST, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    AUTH_002(HttpStatus.BAD_REQUEST, "AUTH_002", "이미 가입된 이메일입니다."),
    AUTH_006(HttpStatus.UNAUTHORIZED, "AUTH_006", "만료되었거나 유효하지 않은 토큰입니다."),

    // Room
    ROOM_001(HttpStatus.NOT_FOUND, "ROOM_001", "존재하지 않는 방입니다."),
    ROOM_002(HttpStatus.FORBIDDEN, "ROOM_002", "해당 방에 접근 권한이 없습니다."),

    // Scan
    SCAN_001(HttpStatus.NOT_FOUND, "SCAN_001", "존재하지 않는 스캔입니다."),
    SCAN_004(HttpStatus.BAD_REQUEST, "SCAN_004", "스캔이 아직 완료되지 않았습니다."),

    // Analysis
    ANALYSIS_001(HttpStatus.NOT_FOUND, "ANALYSIS_001", "분석 결과가 존재하지 않습니다."),
    ANALYSIS_002(HttpStatus.BAD_REQUEST, "ANALYSIS_002", "이미 처리된 분석입니다."),
    ANALYSIS_003(HttpStatus.BAD_REQUEST, "ANALYSIS_003", "선택한 스캔 조합이 올바르지 않거나 비교 가능한 스캔이 부족합니다."),
    ANALYSIS_004(HttpStatus.BAD_REQUEST, "ANALYSIS_004", "분석이 아직 완료되지 않았습니다."),

    // Defect
    DEFECT_001(HttpStatus.NOT_FOUND, "DEFECT_001", "존재하지 않는 하자 정보입니다."),

    // Estimate
    ESTIMATE_001(HttpStatus.BAD_REQUEST, "ESTIMATE_001", "견적 요청 생성에 실패했습니다."),
    ESTIMATE_002(HttpStatus.NOT_FOUND, "ESTIMATE_002", "존재하지 않는 견적 요청입니다."),
    ESTIMATE_003(HttpStatus.FORBIDDEN, "ESTIMATE_003", "해당 견적 요청에 접근 권한이 없습니다."),

    // Repair
    REPAIR_001(HttpStatus.NOT_FOUND, "REPAIR_001", "존재하지 않는 수리 내역입니다."),

    // RepairShop
    REPAIRSHOP_001(HttpStatus.NOT_FOUND, "REPAIRSHOP_001", "추천 가능한 수리 업체가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
