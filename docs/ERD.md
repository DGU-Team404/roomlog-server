Table User {
user_id int [pk, increment] // 사용자 고유 ID
email varchar               // 로그인 이메일 (고유값)
password varchar            // 비밀번호 (OAuth 사용 시 null 가능)
nickname varchar            // 사용자 닉네임
profile_image_url varchar [null] // 프로필 이미지 URL
main_room_id int [ref: > Room.room_id, null] // 대표 방 ID (메인 화면 기준)
is_deleted boolean          // soft delete 여부
created_at datetime         // 생성일
}

Table RefreshToken {
refresh_token_id int [pk, increment] // 리프레시 토큰 ID
user_id int [ref: > User.user_id]    // 사용자 FK
token varchar [unique]               // refresh token 값
expires_at datetime                  // 만료 시간
created_at datetime                 // 생성 시간
is_revoked boolean                  // 폐기 여부 (로그아웃 등)
}

Table Room {
room_id int [pk, increment]         // 방 ID
user_id int [ref: > User.user_id]   // 사용자 FK
name varchar                        // 방 이름 (ex. 자취방)
address varchar                     // 주소
move_in_date date                   // 입주일
move_out_date date                  // 퇴거일
thumbnail_url varchar [null]        // 대표 이미지
created_at datetime                // 생성일
is_deleted boolean                 // soft delete 여부
deleted_at datetime [null]         // 삭제 시각
}

Table Scan {
scan_id int [pk, increment]        // 스캔 ID
room_id int [ref: > Room.room_id, null] // 방 FK (여러 스캔 허용)
file_url varchar [null]            // 3D 파일 URL (S3/R2)
status varchar                     // SCANNING / COMPLETED / FAILED
created_at datetime               // 생성일
thumbnail_url varchar [null]       // 썸네일
scan_type varchar                 // IN / OUT (입주/퇴거)
is_deleted boolean                // soft delete
deleted_at datetime [null]        // 삭제 시각
}

Table Analysis {
analysis_id int [pk, increment]   // 분석 ID
in_room_id int [ref: > Room.room_id]  // 입주 방
out_room_id int [ref: > Room.room_id] // 퇴거 방
in_scan_id int [ref: > Scan.scan_id]  // 입주 스캔
out_scan_id int [ref: > Scan.scan_id] // 퇴거 스캔
total_cost int [null]                // 총 예상 수리비
status varchar                      // PENDING / COMPLETED / FAILED
created_at datetime                // 생성일
is_deleted boolean
deleted_at datetime [null]
}

Table Defect {
defect_id int [pk, increment]      // 하자 ID
analysis_id int [ref: > Analysis.analysis_id] // 분석 FK
type varchar                       // 하자 유형 (벽지, 바닥 등)
severity varchar                   // 심각도 (LOW/MID/HIGH)
location varchar                   // 사람이 읽는 위치 설명
area float                         // 면적
estimated_cost int                 // 예상 비용
before_image_url varchar [null]    // 입주 상태 이미지
after_image_url varchar [null]     // 퇴거 상태 이미지
description varchar [null]         // 상세 설명
x float [null]                     // 3D 좌표 X
y float [null]                     // 3D 좌표 Y
z float [null]                     // 3D 좌표 Z
is_deleted boolean
deleted_at datetime [null]
}

Table Estimate {
estimate_id int [pk, increment]   // 견적 요청 ID
user_id int [ref: > User.user_id] // 사용자
room_id int [ref: > Room.room_id] // 방
analysis_id int [ref: > Analysis.analysis_id] // 분석 결과 기반
provider_name varchar             // 업체명
provider_phone varchar [null]     // 전화번호
provider_address varchar [null]   // 주소
provider_rating float [null]      // 평점
status varchar                    // REQUESTED / SENT / FAILED / COMPLETED
created_at datetime              // 생성일
message varchar [null]            // 요청 메시지
is_deleted boolean
deleted_at datetime [null]
}

Table EstimateDefect {
estimate_defect_id int [pk, increment] // 매핑 ID
estimate_id int [ref: > Estimate.estimate_id] // 견적 FK
defect_id int [ref: > Defect.defect_id]       // 하자 FK
}

Table Repair {
repair_id int [pk, increment]     // 수리 ID
room_id int [ref: > Room.room_id] // 방
estimate_id int [ref: > Estimate.estimate_id, null] // 연결된 견적
provider_name varchar             // 업체명
repair_cost int                   // 실제 수리 비용
status varchar                    // IN_PROGRESS / COMPLETED
repaired_at datetime [null]       // 완료일
note varchar [null]               // 메모
created_at datetime              // 생성일
is_deleted boolean
deleted_at datetime [null]
}

Table RepairDefect {
repair_defect_id int [pk, increment] // 매핑 ID
repair_id int [ref: > Repair.repair_id] // 수리 FK
defect_id int [ref: > Defect.defect_id] // 하자 FK
}