# 기능 명세서 (Feature Specification)

---

## 1. Home (메인)

### H01. 메인 대시보드 조회
- **설명**: 사용자 room 리스트 조회
- **포함 데이터**:
    - 썸네일
    - 최근 스캔 날짜
    - 입주/퇴거일
- **예외 처리**:
    - 데이터 없을 경우 empty UI
- **API**:
    - GET /rooms

---

### H02. 3D 스캔 시작
- **설명**: 스캔 화면 이동
- **예외 처리**:
    - 카메라 권한 요청
- **API**:
    - 없음 (클라이언트 처리)

---

### H03. 방 상세 조회
- **설명**: 선택한 방 상세 정보 조회
- **예외 처리**:
    - 존재하지 않는 room → 404
- **API**:
    - GET /rooms/{roomId}

---

### H04. 방 정보 수정
- **설명**: 방 정보 수정
- **예외 처리**:
    - 404 / 403 / validation
- **API**:
    - PATCH /rooms/{roomId}

---

## 2. Scan (3D 스캔)

### S01. 3D 스캔 수행
- **설명**: LiDAR 기반 공간 캡처
- **예외 처리**:
    - 미지원 기기 제한
- **API**:
    - 없음

---

### S04. 스캔 업로드
- **설명**: 스캔 데이터 서버 저장
- **예외 처리**:
    - 업로드 실패 → retry
- **API**:
    - POST /scans

---

### S05. 방 생성
- **설명**: scan + room 연결
- **예외 처리**:
    - 필수값 누락
- **API**:
    - POST /rooms

---

### S06. 스캔 상태 조회
- **API**:
    - GET /scans/{scanId}/status

---

### S07. 스캔 결과 조회
- **API**:
    - GET /scans/{scanId}

---

## 3. Viewer (하자 분석)

### V03. 하자 분석
- **설명**: 입주/퇴거 비교 분석
- **예외 처리**:
    - 스캔 부족 시 불가
- **API**:
    - POST /analyses
    - GET /analyses/{analysisId}

---

### V05. 하자 상세 조회
- **API**:
    - GET /defects/{defectId}

---

### V06. 수리비 조회
- **API**:
    - GET /analyses/{analysisId}/cost

---

## 4. 수리 업체 추천

### R01. 업체 리스트 조회
- **API**:
    - GET /defects/{defectId}/providers

---

### R03. 업체 상세 조회
- **API**:
    - GET /providers/{providerId}

---

### R04. 견적 요청
- **API**:
    - POST /estimates

---

## 5. Auth / MyPage

### M01. 회원가입
- POST /auth/signup

### M02. 로그인
- POST /auth/login

### M03. 소셜 로그인
- OAuth

### M04. 프로필 조회
- GET /user

### M06. 회원 탈퇴
- DELETE /user