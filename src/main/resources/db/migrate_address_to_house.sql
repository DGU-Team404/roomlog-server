-- ============================================================
-- address 필드: room → house 이동
-- ============================================================

START TRANSACTION;

-- house 테이블에 address 컬럼 추가
ALTER TABLE house
    ADD COLUMN address VARCHAR(255) NULL AFTER name;

-- room 테이블에서 address 컬럼 제거
ALTER TABLE room
    DROP COLUMN address;

COMMIT;
