-- ============================================================
-- house 테이블에 집 아이콘 색상 컬럼 추가 (집 건물 색 + 바닥 색)
-- 기존 행은 NULL 유지, 클라이언트가 기본값으로 폴백 처리
-- ============================================================

START TRANSACTION;

ALTER TABLE house
    ADD COLUMN house_color VARCHAR(30) NULL AFTER address,
    ADD COLUMN floor_color VARCHAR(30) NULL AFTER house_color;

COMMIT;
