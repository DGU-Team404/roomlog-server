-- ============================================================
-- thumbnail_url → file_url 마이그레이션
-- room 테이블: thumbnail_url 컬럼명 → file_url
-- scan 테이블: thumbnail_url 컬럼 제거
-- ============================================================

START TRANSACTION;

-- room.thumbnail_url → room.file_url
ALTER TABLE room
    CHANGE COLUMN thumbnail_url file_url VARCHAR(255) NULL;

-- scan.thumbnail_url 제거
ALTER TABLE scan
    DROP COLUMN thumbnail_url;

COMMIT;
