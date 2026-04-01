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

Table Estimate {
estimate_id int [pk, increment]
user_id int [ref: > User.user_id]
analysis_id int [ref: > Analysis.analysis_id]
provider_name varchar
provider_phone varchar
provider_address varchar
provider_rating float
status varchar // REQUESTED / SENT / RESPONDED / COMPLETED / FAILED
created_at datetime
message varchar
}

Table EstimateDefect {
estimate_defect_id int [pk, increment]
estimate_id int [ref: > Estimate.estimate_id]
defect_id int [ref: > Defect.defect_id]
}