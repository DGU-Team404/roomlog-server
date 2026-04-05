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

H01 메인 대시보드 조회
•	설명: 로그인한 사용자의 대표 방(main room) 요약 정보 + 전체 room 리스트 조회
•	특징:
•	대표 방은 가장 마지막 생성된 방으로 자동 설정
•	사용자가 직접 변경 가능
•	예외: 방 없으면 empty UI
•	API: GET /rooms

⸻

H02 3D 스캔 시작 버튼
•	설명: 스캔 화면으로 이동
•	처리: iOS 클라이언트 처리 (카메라 권한 포함)
•	API: 없음

⸻

H03 방 상세 조회
•	설명: 방 정보 + 간단한 3D 미리보기 제공
•	포함 데이터: 이름, 주소, 썸네일, 최근 스캔 정보
•	예외: 존재하지 않는 room → 404
•	API: GET /rooms/{roomId}

⸻

H04 방 정보 수정
•	설명: 이름, 주소, 입주일, 퇴거일 수정
•	예외:
•	존재하지 않는 room → 404
•	권한 없음 → 403
•	필수값 누락 → 저장 불가
•	API: PATCH /rooms/{roomId}

⸻

H05 대표 방 설정
•	설명: 사용자가 대표 방 직접 선택
•	동작: User.main_room_id 갱신
•	예외:
•	권한 없음 → 403
•	존재하지 않는 room → 404
•	API: PATCH /rooms/{roomId}/main

⸻

2. Scan (3D 스캔)

S01 스캔 수행
•	설명: LiDAR 기반 공간 캡처 (ARKit)
•	예외: LiDAR 미지원 → 기능 제한
•	API: 없음

⸻

S02 스캔 가이드
•	설명: 촬영 가이드 UI 제공
•	예외: 사용자 미이행 시 경고
•	API: 없음

⸻

S03 데이터 처리
•	설명: point cloud → mesh 변환 → 로컬 저장
•	예외: 데이터 부족 시 재촬영
•	API: 없음

⸻

S04 스캔 업로드
•	설명: 3D 파일 + 메타데이터 서버 업로드
•	예외: 업로드 실패 → retry + 캐싱
•	API: POST /scans

⸻

S05 방 생성
•	설명: scan + 메타데이터로 room 생성
•	특징: 생성 후 대표 방 자동 설정
•	예외: 필수값 누락
•	API: POST /rooms

⸻

S06 스캔 상태 조회
•	설명: SCANNING / COMPLETED / FAILED 상태 반환
•	API: GET /scans/{scanId}/status

⸻

S07 스캔 결과 미리보기
•	설명: 업로드 전 결과 확인
•	예외: 데이터 없음 → 재촬영
•	API: GET /scans/{scanId}

⸻

3. Viewer (3D / 분석)

V01 단일 스캔 조회
•	설명: 3D 모델(.ply) 조회
•	예외: 데이터 없음 → empty view
•	API: GET /scans/{scanId}

⸻

V02 스캔 목록 조회
•	설명: 입주/퇴거 비교용 스캔 목록 제공
•	예외: 스캔 부족 시 비활성화
•	API: GET /rooms/{roomId}/scans

⸻

V02-1 분석 생성
•	설명: 선택한 scan 기반 분석 요청
•	예외: 스캔 부족
•	API: POST /analyses

⸻

V03 분석 결과 조회
•	설명: 하자 리스트 + 요약 정보 반환
•	예외: 분석 없음 → empty
•	API: GET /analyses/{analysisId}

⸻

V04 하자 위치 표시
•	설명: 3D 위에 하자 마커 표시
•	예외: 좌표 없음 → 리스트만 표시
•	API: 없음 (데이터 활용)

⸻

V05 하자 상세 조회
•	설명: 유형, 면적, 위치, 비용, 이미지
•	예외: defect 없음 → 404
•	API: GET /defects/{defectId}

⸻

V06 수리비 요약
•	설명: 총 비용 + 항목별 비용
•	예외: 데이터 없음 → “산정 불가”
•	API: GET /analyses/{analysisId}/cost

⸻

4. Defect

D01 하자 화면 진입
•	설명: room 기반 하자 관리 화면 진입
•	예외: room 없음 → 404
•	API: GET /rooms/{roomId}

⸻

5. 수리 업체

R01 업체 리스트 조회
•	설명: 카카오 API 기반 주변 업체 조회
•	파라미터: type, radius, sort
•	API:
GET /analyses/{analysisId}/repair-shops

⸻

R02 견적 요청
•	설명: 하자 + 업체 정보 저장
•	API: POST /estimates

⸻

R03 견적 목록 조회
•	설명: 사용자 견적 요청 리스트 조회
•	API: GET /estimates

⸻

R04 견적 상세 조회
•	설명: 메시지, 하자, 업체, 상태 조회
•	API: GET /estimates/{estimateId}

⸻

R05 수리 완료 목록 조회
•	설명: room 기준 수리 이력 조회
•	API: GET /rooms/{roomId}/repairs

⸻

R06 수리 완료 처리
•	설명: 상태 IN_PROGRESS → COMPLETED 변경
•	예외:
•	repair 없음 → 404
•	권한 없음 → 403
•	API: PATCH /repairs/{repairId}

⸻

6. Auth

A01 회원가입
•	설명: 이메일, 비밀번호, 닉네임 기반 가입
•	API: POST /auth/signup

⸻

A02 로그인
•	설명: JWT 발급
•	API: POST /auth/login

⸻

7. MyPage

M01 프로필 조회
•	설명: 사용자 정보 조회
•	API: GET /user

⸻

M02 내 정보 수정
•	설명: 닉네임 수정
•	API: PATCH /user

⸻

M03 로그아웃
•	설명: 토큰 삭제
•	처리: 클라이언트
•	API: 없음

⸻

M04 회원 탈퇴
•	설명: soft delete 처리
•	API: DELETE /user

⸻