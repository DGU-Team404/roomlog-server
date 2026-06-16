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
├── auth
│   ├── controller   AuthController          — 회원가입 · 로그인 · 토큰 재발급 엔드포인트
│   ├── domain       RefreshToken            — Refresh Token 엔티티
│   ├── dto          LoginRequest/Response, SignupRequest/Response, ReissueRequest/Response
│   ├── repository   RefreshTokenRepository
│   └── service      AuthService             — 회원가입 · 로그인 · JWT 재발급 · 비밀번호 암호화
│
├── user
│   ├── controller   UserController          — 프로필 조회 · 수정 · 탈퇴 엔드포인트
│   ├── domain       User                    — 사용자 엔티티
│   ├── dto          GetMyProfileResponse, UpdateUserRequest/Response, DeleteUserResponse
│   ├── repository   UserRepository
│   └── service      UserService
│
├── house
│   ├── controller   HouseController         — 집 CRUD · 대표 집 설정 엔드포인트
│   ├── domain       House                   — 집 엔티티 (주소 · 입주일 · 퇴거일)
│   ├── dto          CreateHouseRequest/Response, UpdateHouseRequest/Response,
│   │                GetHousesResponse, GetHouseRoomsResponse,
│   │                HouseListItemResponse, SetMainHouseResponse, DeleteHouseResponse
│   ├── repository   HouseRepository
│   └── service      HouseService
│
├── room
│   ├── controller   RoomController          — 방 CRUD · 대표 방 설정 엔드포인트
│   ├── domain       Room                    — 방 엔티티 (이름 · 면적 · 구조)
│   ├── dto          CreateRoomRequest/Response, UpdateRoomRequest/Response,
│   │                GetRoomsResponse, GetRoomDetailResponse,
│   │                RoomListItemResponse, SetMainRoomResponse, DeleteRoomResponse
│   ├── repository   RoomRepository
│   └── service      RoomService
│
├── scan
│   ├── controller   ScanController          — 스캔 업로드 · 조회 · 상태 폴링 · AI 콜백 엔드포인트
│   ├── domain       Scan                    — 스캔 엔티티 (상태 · PLY URL · 썸네일 URL)
│   ├── dto          CreateScanRequest/Response, GetScanResponse, GetScanStatusResponse,
│   │                RoomScanListItemResponse,
│   │                AiReconstructionRequest, AiReconstructionResult  — AI 서버 연동 DTO
│   ├── repository   ScanRepository
│   └── service      ScanService             — R2 업로드 → AI 비동기 요청 → 콜백 처리
│
├── analysis
│   ├── controller   AnalysisController      — 분석 생성 · 조회 · 상태 폴링 · AI 콜백 · 수리 업체 검색 엔드포인트
│   ├── domain       Analysis                — 분석 엔티티 (단일/비교 타입 · 상태 · 요약)
│   ├── dto          CreateAnalysisRequest/Response, GetAnalysisResponse, GetAnalysisStatusResponse,
│   │                GetComparisonAnalysisListResponse, DeleteAnalysisResponse,
│   │                GetRepairShopsResponse, RepairShopResponse, RepairShopSearchCondition,
│   │                AiDetectionRequest, AiCompareRequest, AiResultRequest  — AI 서버 연동 DTO
│   ├── repository   AnalysisRepository
│   └── service      AnalysisService         — 단일/비교 하자 분석 요청 및 콜백 처리
│                    RepairShopService       — Kakao Local API로 주변 수리 업체 검색
│
├── defect
│   ├── domain       Defect                  — 하자 엔티티 (종류 · 심각도 · 영역 좌표 · 예상 비용)
│   │                DefectUnitPrice         — 하자 종류별 단가 테이블
│   │                SeverityMultiplier      — 심각도 배율 (LOW · MEDIUM · HIGH)
│   │                RegionPointListConverter — 영역 좌표 JSON 직렬화 컨버터
│   ├── dto          DefectItemResponse, GetDefectEntryResponse, RegionPoint
│   ├── repository   DefectRepository, DefectUnitPriceRepository
│   └── service      DefectService           — 하자 비용 산정 (단가 × 면적 × 심각도 배율)
│
├── estimate
│   ├── controller   EstimateController      — 견적 생성 · 목록 · 상세 · 미리보기 엔드포인트
│   ├── domain       Estimate                — 견적 엔티티 (업체명 · 요청 금액 · 상태)
│   │                EstimateDefect          — 견적-하자 연관 엔티티
│   ├── dto          CreateEstimateRequest/Response, GetEstimateListResponse,
│   │                GetEstimateDetailResponse, EstimateListItemResponse,
│   │                EstimatePreviewRequest/Response
│   ├── repository   EstimateRepository, EstimateDefectRepository
│   └── service      EstimateService         — 견적 생성 · 미리보기 금액 계산 · 수리 완료 처리
│
├── repair
│   ├── domain       Repair                  — 수리 이력 엔티티 (업체명 · 실비용 · 메모)
│   │                RepairDefect            — 수리-하자 연관 엔티티
│   ├── dto          CreateRepairRequest/Response, GetRepairListResponse, RepairListItemResponse
│   ├── repository   RepairRepository, RepairDefectRepository
│   └── service      RepairService           — 수리 이력 저장 · 연관 견적 상태 COMPLETED 갱신
│
└── global
    ├── config
    │   ├── AppConfig                — RestClient 빈 등록
    │   ├── DefectUnitPriceInitializer — 서버 기동 시 단가 테이블 초기화
    │   ├── JacksonConfig            — LocalDate 직렬화 설정
    │   ├── R2Config                 — Cloudflare R2 S3Client 빈 등록
    │   ├── SecurityConfig           — Spring Security 필터 체인 · CORS 설정
    │   └── SwaggerConfig            — SpringDoc OpenAPI 설정
    ├── exception
    │   ├── ErrorCode                — 에러 코드 열거형 (HTTP 상태 · 메시지)
    │   ├── CustomException          — ErrorCode를 wrapping한 런타임 예외
    │   └── GlobalExceptionHandler   — @RestControllerAdvice 전역 예외 처리
    ├── infra
    │   ├── AiClient                 — AI 서버 HTTP 클라이언트 (스캔 재구성 · 하자 탐지 · 비교)
    │   ├── KakaoLocalClient         — Kakao Local API HTTP 클라이언트
    │   └── R2FileUploader           — Cloudflare R2 파일 업로드 유틸
    ├── response
    │   └── ApiResponse              — 공통 응답 래퍼 { success, data, message }
    └── security
        ├── JwtAuthenticationFilter  — JWT 검증 · SecurityContext 주입 필터
        ├── ApiKeyFilter             — AI 콜백 엔드포인트용 API Key 검증 필터
        ├── AuthToken                — Authentication 구현체
        └── LoginUser                — @AuthenticationPrincipal 바인딩용 DTO
```

## ⚙️ 환경 변수

프로덕션 환경(`--spring.profiles.active=prod`)에서 아래 환경 변수가 필요합니다.

| 환경 변수 | 설명 | 예시 |
|-----------|------|------|
| `PORT` | 서버 포트 (기본값: `8080`) | `8080` |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) | `roomlog-secret-key-...` |
| `AI_API_KEY` | AI 서버 인증 키 (콜백 엔드포인트 보호용) | `roomlog-ai-secret-key` |
| `AI_SERVER_URL` | AI 서버 베이스 URL | `http://43.205.192.197` |
| `AI_CALLBACK_BASE_URL` | AI 콜백 수신 URL (이 서버의 외부 주소) | `https://roomlog-server-production.up.railway.app` |
| `KAKAO_API_KEY` | Kakao REST API 키 (수리 업체 검색) | — |
| `SPRING_DATASOURCE_URL` | MySQL JDBC URL | `jdbc:mysql://host:3306/roomlog?serverTimezone=Asia/Seoul` |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자명 | `root` |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | — |
| `R2_ACCOUNT_ID` | Cloudflare R2 계정 ID | — |
| `R2_ACCESS_KEY` | R2 액세스 키 | — |
| `R2_SECRET_KEY` | R2 시크릿 키 | — |
| `R2_BUCKET` | R2 버킷 이름 | `roomlog-bucket` |
| `R2_CDN_URL` | R2 퍼블릭 CDN URL | `https://pub-xxx.r2.dev` |

> 로컬 개발 환경(`--spring.profiles.active=dev`)은 `application-dev.yml`에 직접 값을 설정합니다.

## 📄 License

[MIT License](LICENSE) © 2026 DGU Team404
