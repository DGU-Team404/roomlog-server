Table User {
user_id int [pk, increment]
email varchar
password varchar
nickname varchar
profile_image_url varchar [null]
main_room_id int [ref: > Room.room_id, null]
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
thumbnail_url varchar [null]
created_at datetime
is_deleted boolean
deleted_at datetime [null]
}

Table Scan {
scan_id int [pk, increment]
room_id int [ref: > Room.room_id, null, unique]
file_url varchar [null]
status varchar // SCANNING / COMPLETED / FAILED
created_at datetime
thumbnail_url varchar [null]
scan_type varchar // IN / OUT
is_deleted boolean
deleted_at datetime [null]
}

Table Analysis {
analysis_id int [ref: > Analysis.analysis_id, null]
in_room_id int [ref: > Room.room_id]
out_room_id int [ref: > Room.room_id]
in_scan_id int [ref: > Scan.scan_id]
out_scan_id int [ref: > Scan.scan_id]
total_cost int [null]
status varchar // PENDING / COMPLETED / FAILED
created_at datetime
is_deleted boolean
deleted_at datetime [null]
}

Table Defect {
defect_id int [pk, increment]
analysis_id int [ref: > Analysis.analysis_id]
type varchar
severity varchar
location varchar
area float
estimated_cost int
before_image_url varchar [null]
after_image_url varchar [null]
description varchar [null]
x float [null]
y float [null]
z float [null]
is_deleted boolean
deleted_at datetime [null]
}

Table Estimate {
estimate_id int [pk, increment]
user_id int [ref: > User.user_id]
room_id int [ref: > Room.room_id]
analysis_id int [ref: > Analysis.analysis_id]
provider_name varchar
provider_phone varchar [null]
provider_address varchar [null]
provider_rating float [null]
status varchar // REQUESTED / SENT / FAILED / COMPLETED
created_at datetime
message varchar [null]
is_deleted boolean
deleted_at datetime [null]
}

Table EstimateDefect {
estimate_defect_id int [pk, increment]
estimate_id int [ref: > Estimate.estimate_id]
defect_id int [ref: > Defect.defect_id]
}

Table Repair {
repair_id int [pk, increment]
room_id int [ref: > Room.room_id]
estimate_id int [ref: > Estimate.estimate_id, null]
provider_name varchar
repair_cost int
status varchar // IN_PROGRESS / COMPLETED
repaired_at datetime [null]
note varchar [null]
created_at datetime
is_deleted boolean
deleted_at datetime [null]
}

Table RepairDefect {
repair_defect_id int [pk, increment]
repair_id int [ref: > Repair.repair_id]
defect_id int [ref: > Defect.defect_id]
}