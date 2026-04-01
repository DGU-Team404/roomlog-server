COMMON_400 : 잘못된 요청입니다. #400 Bad Request
COMMON_401 : 인증이 필요합니다. #401 Unauthorized
COMMON_403 : 접근 권한이 없습니다. #403 Forbidden
COMMON_404 : 요청한 데이터를 찾을 수 없습니다. #404 Not Found
COMMON_500 : 서버 내부 오류가 발생했습니다. #500 Internal Server Error

AUTH_001 : 이메일 또는 비밀번호가 올바르지 않습니다. #400 Bad Request
AUTH_002 : 이미 가입된 이메일입니다. #400 Bad Request
AUTH_006 : 만료되었거나 유효하지 않은 토큰입니다. #401 Unauthorized

ROOM_001 : 존재하지 않는 방입니다. #404 Not Found
ROOM_002 : 해당 방에 접근 권한이 없습니다. #403 Forbidden

SCAN_001 : 존재하지 않는 스캔입니다. #404 Not Found
SCAN_004 : 스캔이 아직 완료되지 않았습니다. #400 Bad Request

ANALYSIS_001 : 분석 결과가 존재하지 않습니다. #404 Not Found
ANALYSIS_003 : 비교 가능한 입주/퇴거 스캔이 부족합니다. #400 Bad Request
ANALYSIS_004 : 분석이 아직 완료되지 않았습니다. #400 Bad Request
ANALYSIS_005 : 추천 가능한 수리 업체가 없습니다. #404 Not Found

DEFECT_001 : 존재하지 않는 하자 정보입니다. #404 Not Found

ESTIMATE_001 : 견적 요청 생성에 실패했습니다. #500 Internal Server Error
ESTIMATE_002 : 존재하지 않는 견적 요청입니다. #404 Not Found
ESTIMATE_003 : 해당 견적 요청에 접근 권한이 없습니다. #403 Forbidden