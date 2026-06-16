# roomlog-Server

> 자취/원룸 거주자를 위한 **3D 방 기록 & 하자 관리 서비스**의 백엔드 서버
> 2026 동국대학교 정보통신공학과 졸업프로젝트 (DGU-ICE Capstone Design, Team 404)

LiDAR로 촬영한 방을 3D로 재구성하고, AI가 하자를 자동 탐지해 수리 견적까지 연결하는 서비스의 Spring Boot 백엔드입니다.

## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| **스캔 수신** | 모바일에서 업로드한 LiDAR 파일(.zip)을 Cloudflare R2에 저장하고 AI 서버에 3D 재구성을 요청합니다. |
| **하자 탐지** | 단일 방 하자 탐지 또는 입주 전·퇴거 후 두 방 스캔을 비교해 새로 생긴 하자만 추출합니다. 하자별 심각도와 수리 예상 비용을 함께 산정합니다. |
| **수리 업체 연결** | 방 주소를 기반으로 Kakao Local API에서 주변 수리 업체를 검색하고 견적 요청 이력을 저장합니다. |
| **수리 이력 관리** | 수리 완료 후 업체명·비용·메모를 기록하고 견적 상태를 COMPLETED로 갱신합니다. |

## 🚀 백엔드 포인트

### 비동기 AI 파이프라인

스캔 업로드와 하자 분석 모두 처리 시간이 길어 동기 응답이 불가합니다. AI 서버가 작업을 완료하면 콜백 엔드포인트(`POST /scans/{id}/result`, `POST /analyses/{id}/result`)로 결과를 전송하고, 클라이언트는 상태 폴링(`/status`)으로 완료 여부를 확인합니다.

```
업로드 요청  →  R2 저장  →  AI 서버 비동기 요청
                                    ↓ (완료 후 콜백)
폴링 (SCANNING → COMPLETED)  ←  결과 수신 및 DB 반영
```

### 계층형 데이터 구조

집 → 방 → 스캔 → 분석 → 하자로 이어지는 계층 구조를 JPA로 관리합니다. 상위 엔티티 삭제 시 하위 데이터가 일괄 삭제되어 고아 데이터가 남지 않습니다.

### 이중 인증 분리

- **사용자 API** — `Authorization: Bearer {accessToken}` (JWT)
- **AI 콜백 API** — `X-Api-Key` 헤더 (내부 전용, 외부 노출 차단)

## 🛠 기술 스택

- **언어/프레임워크**: Java 17, Spring Boot 3.2.4
- **보안**: Spring Security, JWT (Access 24h / Refresh 14d)
- **데이터**: MySQL, Spring Data JPA
- **파일 저장**: Cloudflare R2 (AWS S3 호환)
- **외부 API**: Kakao Local API (수리 업체 검색), AI Server (LiDAR 재구성 / 하자 탐지)
- **문서**: SpringDoc OpenAPI (Swagger UI)

## 🏛 패키지 구조

```text
com.roomlog
├── auth          — 인증 (회원가입 · 로그인 · JWT 재발급)
├── user          — 사용자 프로필
├── house         — 집 관리
├── room          — 방 관리
├── scan          — LiDAR 스캔 업로드 및 3D 재구성
├── analysis      — 하자 분석 (단일 / 비교)
├── defect        — 하자 정보 및 비용 산정
├── estimate      — 견적 요청 및 수리 업체 연결
├── repair        — 수리 완료 이력
└── global
    ├── config    — Security · Swagger · R2 · Jackson 설정
    ├── exception — 에러 코드 및 전역 예외 처리
    ├── infra     — AI 클라이언트 · Kakao API 클라이언트 · R2 업로더
    ├── response  — 공통 API 응답 형식
    └── security  — JWT 필터 · API Key 필터
```

## 📄 License

[MIT License](LICENSE) © 2026 DGU Team404
