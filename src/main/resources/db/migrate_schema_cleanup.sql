-- ============================================================
-- DB 스키마 정리 마이그레이션
-- 엔티티 코드 기준으로 실제 DB 스키마를 맞춤
-- 실행 전: Railway DB 백업 권장
-- ============================================================

START TRANSACTION;

-- ============================================================
-- 1. scan 테이블 정리
--    - scan_type: 엔티티에 없음 → 제거 (분석 생성 시 in/out 결정)
--    - thumbnail_url: 엔티티에 없음 → 제거
--    - file_url: varchar(255) → varchar(2000) (AI 결과 URL이 길어 500 오류 원인)
-- ============================================================
ALTER TABLE scan
    DROP COLUMN IF EXISTS scan_type,
    DROP COLUMN IF EXISTS thumbnail_url,
    MODIFY COLUMN file_url VARCHAR(2000) DEFAULT NULL;

-- ============================================================
-- 2. defect 테이블 정리
--    - x, y, z: 이전 스키마 컬럼 → region_3d TEXT로 교체됨
--    - region_3d: 엔티티에 있음 → 없으면 추가
-- ============================================================
ALTER TABLE defect
    DROP COLUMN IF EXISTS x,
    DROP COLUMN IF EXISTS y,
    DROP COLUMN IF EXISTS z;

-- region_3d 컬럼이 없으면 추가
ALTER TABLE defect
    ADD COLUMN IF NOT EXISTS region_3d TEXT DEFAULT NULL;

-- ============================================================
-- 3. user 테이블 정리
--    - main_room_id: 이전 마이그레이션에서 주석 처리로 남아있을 수 있음
-- ============================================================
ALTER TABLE `user`
    DROP COLUMN IF EXISTS main_room_id;

COMMIT;

-- ============================================================
-- 검증 쿼리 (실행 후 확인용)
-- ============================================================
-- SHOW COLUMNS FROM scan;
-- SHOW COLUMNS FROM defect;
-- SHOW COLUMNS FROM `user`;
