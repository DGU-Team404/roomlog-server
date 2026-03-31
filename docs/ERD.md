# ERD (Entity Relationship Diagram)

RoomLog 서비스의 데이터베이스 구조를 정의한다.  
사용자는 방을 생성하고, 방에 대한 스캔 데이터를 저장하며, 입주/퇴거 스캔 비교를 통해 하자를 분석하고, 추천 업체 조회 및 견적 요청까지 진행할 수 있다.

---

## 1. ERD 정의

Table User {
  user_id int [pk, increment]
  email varchar
  password varchar
  nickname varchar
  is_deleted boolean
  created_at datetime
}

Table Room {
  room_id int [pk, increment]
  user_id int [ref: > User.user_id]
  name varchar
  address varchar
  move_in_date date
  move_out_date date
  thumbnail_url varchar
  created_at datetime
}

Table Scan {
  scan_id int [pk, increment]
  room_id int [ref: > Room.room_id, null]
  scan_type varchar // IN / OUT (nullable)
  file_url varchar
  status varchar // SCANNING / COMPLETED / FAILED
  created_at datetime
  thumbnail_url varchar
}

Table Analysis {
  analysis_id int [pk, increment]
  room_id int [ref: > Room.room_id]
  in_scan_id int [ref: > Scan.scan_id]
  out_scan_id int [ref: > Scan.scan_id]
  total_cost int
  status varchar // PENDING / COMPLETED / FAILED
  created_at datetime
}

Table Defect {
  defect_id int [pk, increment]
  analysis_id int [ref: > Analysis.analysis_id]
  type varchar
  severity varchar
  location varchar
  area float
  estimated_cost int
  before_image_url varchar
  after_image_url varchar
  description varchar
  x float
  y float
  z float
}

Table Provider {
  provider_id int [pk, increment]
  name varchar
  phone varchar
  address varchar
  rating float
  min_cost int
  max_cost int
  latitude float
  longitude float
}

Table Estimate {
  estimate_id int [pk, increment]
  user_id int [ref: > User.user_id]
  provider_id int [ref: > Provider.provider_id]
  defect_id int [ref: > Defect.defect_id]
  status varchar // REQUESTED / FAILED / COMPLETED
  created_at datetime
  message varchar
}