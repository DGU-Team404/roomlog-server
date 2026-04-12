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

1. Home (메인)

H01 - 메인 대시보드 조회
•	API: GET /rooms
•	설명:
로그인한 사용자의 대표 방(main room) 요약 정보와 전체 room 리스트를 조회한다.
대표 방은 최초 방 생성 시 가장 마지막에 생성된 방으로 자동 설정되며, 사용자가 직접 변경한 이후에는 해당 설정을 우선 적용한다.
•	예외 처리:
등록된 방이 없을 경우 empty UI (“저장된 방이 없습니다”)

⸻

H02 - 3D 스캔 시작 버튼
•	설명: 스캔 화면 이동
•	비고: iOS 처리

⸻

H03 - 방 상세 조회
•	API: GET /rooms/{roomId}
•	설명:
방 이름, 주소, 대표 이미지, 연결된 스캔 정보 등 기본 정보를 조회하고,
저장된 3D 모델을 간단한 회전 형태로 미리 확인할 수 있다.
•	예외 처리:
존재하지 않는 room → 404

⸻

H04 - 방 정보 수정
•	API: PATCH /rooms/{roomId}
•	설명:
방 이름, 주소, 입주일, 퇴거일 수정
•	예외 처리:
•	404 (존재하지 않는 방)
•	403 (권한 없음)
•	필수값 누락

⸻

H05 - 대표 방 설정
•	API: PATCH /rooms/{roomId}/main
•	설명:
선택한 room을 대표 방으로 설정하고 User.main_room_id 갱신
•	예외 처리:
403 / 404

⸻

H06 - 방 삭제
•	API: DELETE /rooms/{roomId}
•	설명:
soft delete 처리 (scan, analysis, defect, estimate, repair 포함)
•	예외 처리:
•	404 / 403
•	대표 방 삭제 시 → 다른 방으로 자동 변경 or null

⸻

2. Scan (3D 스캔)

S04 - 스캔 업로드
•	API: POST /scans
•	설명:
LiDAR 스캔 결과 업로드 (임시 저장)

⸻

S05 - 방 생성
•	API: POST /rooms
•	설명:
scan_type(IN/OUT)과 함께 room 생성
→ scan 연결
→ room당 scan 1개

⸻

S06 - 스캔 상태 조회
•	API: GET /scans/{scanId}/status
•	설명:
UPLOADING / COMPLETED / FAILED 상태 조회

⸻

S07 - 스캔 미리보기
•	API: GET /scans/{scanId}/preview

⸻

3. Viewer (3D 조회 / 비교)

V01 - 3D Viewer
•	API: GET /scans/{scanId}/viewer
•	설명:
.ply 기반 3D 파일 조회

⸻

V02 - 비교 시점 선택
•	API:

GET /rooms?scanType=IN
GET /rooms?scanType=OUT

	•	설명:
입주/퇴거 room 선택

⸻

V02-1 - 분석 생성
•	API: POST /analyses
•	설명:
in/out room 기반 scan 비교 분석 생성

⸻

V03 - 분석 결과 조회
•	API: GET /analyses/{analysisId}
•	설명:
하자 리스트 및 요약 조회

⸻

V05 - 하자 상세 조회
•	API: GET /defects/{defectId}

⸻

V06 - 수리비 요약
•	API: GET /analyses/{analysisId}/cost

⸻

4. Defect (하자)

D01 - 방 하자 목록 조회
•	API: GET /rooms/{roomId}/defects
•	설명:
room 기준 하자 리스트 조회

⸻

5. 수리 업체 추천

R01 - 업체 리스트 조회
•	API:

GET /analyses/{analysisId}/repair-shops

	•	Query:

type, radius, sort

⸻

R02 - 견적 요청 미리보기
•	API: POST /estimates/preview
•	설명:
문자 문의 미리보기

⸻

R02-1 - 견적 요청
•	API: POST /estimates
•	설명:
문자 문의용 템플릿 생성 + 저장

⸻

R03 - 견적 목록 조회
•	API: GET /estimates

⸻

R04 - 견적 상세 조회
•	API: GET /estimates/{estimateId}

⸻

R05 - 수리 내역 조회
•	API: GET /rooms/{roomId}/repairs

⸻

R06 - 수리 완료 등록
•	API:

PATCH /estimates/{estimateId}/complete

	•	설명:
estimate 기반 repair 생성

⸻

6. Auth

A01 - 회원가입
•	API: POST /auth/signup

A02 - 로그인
•	API: POST /auth/login

A03 - 토큰 재발급
•	API: POST /auth/refresh

⸻

7. MyPage

M01 - 프로필 조회
•	API: GET /user

M02 - 내 정보 수정
•	API: PATCH /user

M03 - 로그아웃
•	설명: 클라이언트 처리

M04 - 회원 탈퇴
•	API: DELETE /user
