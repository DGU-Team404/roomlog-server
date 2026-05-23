-- move_in_date, move_out_date NULL 허용으로 변경
ALTER TABLE room MODIFY COLUMN move_in_date DATE NULL;
ALTER TABLE room MODIFY COLUMN move_out_date DATE NULL;