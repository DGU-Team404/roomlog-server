#### **성공 응답 형식 예시:**

```yaml
{
  "success": true,
  "code": 200,
  "message": "방 목록 조회 성공",
  "data": {
    "rooms": [
      {
        "room_id": 1,
        "name": "자취방",
        "thumbnail_url": "...",
        "recent_scan_date": "2026-03-29"
      }
    ]
  }
}
```

#### **실패 응답 형식 예시:**

```yaml
{
  "success": false,
  "code": 400,
  "message": "사용자를 찾을 수 없습니다.",
  "error": {
    "code": "AUTH_005"
  },
  "data": null
}
```

⸻

📘 RoomLog API 명세서 (개선 버전)

⸻

1. Home (메인)

⸻

🔹 H01 - GET /rooms

[기능 목적]

로그인한 사용자가 등록한 방 목록을 조회하여 메인 대시보드에 표시

[동작 흐름]
•	앱 실행 후 메인 진입 시 호출
•	사용자별 room 리스트 조회
•	각 방의 최근 상태를 함께 내려줌

[응답 데이터]
•	room_id
•	name
•	address
•	thumbnail_url
•	move_in_date / move_out_date
•	latest_scan (scan_id, scan_type)
•	recent_scan_date
•	latest_scan_status

[예외 처리]
•	방이 없을 경우 빈 배열 반환 → empty UI

⸻

🔹 H03 - GET /rooms/{roomId}

[기능 목적]

특정 방의 상세 정보를 조회하여 방 상세 화면 구성

[동작 흐름]
•	사용자가 특정 room 선택 시 호출
•	room_id 기반 데이터 조회

[응답 데이터]
•	room 기본 정보
•	대표 썸네일
•	최근 스캔 정보

[예외 처리]
•	존재하지 않는 room_id → 404

⸻

🔹 H04 - PATCH /rooms/{roomId}

[기능 목적]

방의 기본 정보 수정

[동작 흐름]
•	사용자 입력 값으로 room 정보 업데이트

[요청 데이터]
•	name
•	address
•	move_in_date
•	move_out_date

[예외 처리]
•	존재하지 않는 room → 404
•	권한 없음 → 403
•	필수값 누락 → 400

⸻

2. Scan (3D 스캔)

⸻

🔹 S04 - POST /scans

[기능 목적]

3D 스캔 결과 파일을 서버에 업로드

[동작 흐름]
•	클라이언트에서 생성된 3D 모델 업로드
•	scan_id 생성 후 저장

[요청 데이터]
•	file (3D 모델 파일)
•	metadata (선택)

[응답 데이터]
•	scan_id
•	status (SCANNING / COMPLETED)

[예외 처리]
•	업로드 실패 → retry

⸻

🔹 S05 - POST /rooms

[기능 목적]

방 생성 + 업로드된 scan 연결

[동작 흐름]
•	scan_id와 함께 room 생성
•	scan.room_id 업데이트

[요청 데이터]
•	name
•	address
•	move_in_date
•	move_out_date
•	scan_id

[예외 처리]
•	필수값 누락 → 400

⸻

🔹 S06 - GET /scans/{scanId}/status

[기능 목적]

스캔 처리 상태 조회

[동작 흐름]
•	scan 진행 상태 polling

[응답 데이터]
•	status (SCANNING / COMPLETED / FAILED)

⸻

🔹 S07 - GET /scans/{scanId}

[기능 목적]

완료된 스캔 결과 조회

[응답 데이터]
•	file_url
•	thumbnail_url
•	status

[예외 처리]
•	데이터 없음 → 재촬영 유도

⸻

3. Viewer (하자 분석)

⸻

🔹 V01 - GET /scans/{scanId}

(동일, 재사용 API)

⸻

🔹 V02 - GET /rooms/{roomId}/scans

[기능 목적]

방의 스캔 목록 조회 (비교용)

[동작 흐름]
•	IN / OUT 스캔 선택 UI 구성

[응답 데이터]
•	scan_id
•	scan_type
•	created_at

⸻

🔹 V03 - POST /analyses

[기능 목적]

입주/퇴거 스캔 비교 분석 생성

[동작 흐름]
•	in_scan_id + out_scan_id 전달
•	분석 작업 생성 (비동기 가능)
•	상태: PENDING → COMPLETED

[요청 데이터]
•	in_scan_id
•	out_scan_id

[응답 데이터]
•	analysis_id
•	status

[예외 처리]
•	동일 scan 비교 → 400
•	scan 없음 → 404

⸻

🔹 V03-2 - GET /analyses/{analysisId}

[기능 목적]

분석 결과 조회

[응답 데이터]
•	analysis_id
•	status
•	total_cost
•	defects (list)
•	defect_id
•	type
•	severity
•	location
•	area

[예외 처리]
•	분석 미완료 → status=PENDING

⸻

🔹 V05 - GET /defects/{defectId}

[기능 목적]

하자 상세 조회

[응답 데이터]
•	type
•	severity
•	location
•	area
•	estimated_cost
•	before_image_url
•	after_image_url
•	x, y, z

[예외 처리]
•	없음 → 404

⸻

🔹 V06 - GET /analyses/{analysisId}/cost

[기능 목적]

전체 수리비 요약 조회

[응답 데이터]
•	total_cost
•	항목별 비용 리스트

[예외 처리]
•	비용 없음 → “산정 불가”

⸻

4. 수리 업체 추천

⸻

🔹 R01 - GET /analyses/{analysisId}/repair-shops

[기능 목적]

분석 결과 기반 주변 업체 추천

[동작 흐름]
•	analysis → defect 유형 추출
•	위치 기반 외부 API 호출
•	필터링 후 반환

[쿼리 파라미터]
•	type
•	radius
•	sort

[응답 데이터]
•	업체명
•	전화번호
•	주소
•	평점

[예외 처리]
•	위치 없음 → 수동 입력 요청

⸻

🔹 R03 - POST /estimates

[기능 목적]

견적 요청 생성

[동작 흐름]
•	선택한 defect들 + 업체 정보 저장
•	Estimate + EstimateDefect 생성

[요청 데이터]
•	analysis_id
•	provider_name
•	provider_phone
•	defect_ids (list)
•	message

[응답 데이터]
•	estimate_id
•	status

[예외 처리]
•	저장 실패 → retry

⸻

🔹 R04 - GET /estimates

[기능 목적]

사용자 견적 요청 목록 조회

[응답 데이터]
•	estimate_id
•	provider_name
•	status
•	created_at

⸻

🔹 R05 - GET /estimates/{estimateId}

[기능 목적]

견적 요청 상세 조회

[응답 데이터]
•	업체 정보
•	요청 메시지
•	defect 목록
•	상태

⸻

5. MyPage / 로그인

⸻

🔹 M01 - POST /auth/signup

[기능 목적]

회원가입

[예외 처리]
•	이메일 중복

⸻

🔹 M02 - POST /auth/login

[기능 목적]

JWT 기반 로그인

[응답 데이터]
•	access_token

⸻

🔹 M03 - POST /auth/oauth

[기능 목적]

소셜 로그인

⸻

🔹 M04 - GET /user

[기능 목적]

내 정보 조회

⸻

🔹 M06 - DELETE /user

[기능 목적]

회원 탈퇴 (soft delete)

⸻