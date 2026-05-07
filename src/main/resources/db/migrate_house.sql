-- ============================================================
-- House 도메인 마이그레이션
-- 실행 전: 반드시 DB 백업 후 진행할 것
-- 실행 순서: 이 파일 전체를 트랜잭션으로 실행
-- ============================================================

START TRANSACTION;

-- ============================================================
-- STEP 1. houses 테이블 생성
-- ============================================================
CREATE TABLE IF NOT EXISTS house (
    house_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL,
    is_deleted TINYINT(1)   NOT NULL DEFAULT 0,
    deleted_at DATETIME(6),
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (house_id)
);

-- ============================================================
-- STEP 2. rooms 테이블에 house_id 컬럼 추가 (우선 nullable)
-- ============================================================
ALTER TABLE room
    ADD COLUMN house_id BIGINT NULL AFTER user_id;

-- ============================================================
-- STEP 3. 기존 유저별 집 생성 (유저 1명당 집 1개)
--         이름은 '내 집'으로 기본 설정
-- ============================================================
INSERT INTO house (user_id, name, is_deleted, created_at)
SELECT DISTINCT
    r.user_id,
    '내 집',
    0,
    NOW()
FROM room r
WHERE r.is_deleted = 0;

-- ============================================================
-- STEP 4. rooms.house_id 업데이트
--         각 room의 user_id와 일치하는 house로 연결
-- ============================================================
UPDATE room r
JOIN house h ON h.user_id = r.user_id
SET r.house_id = h.house_id
WHERE r.is_deleted = 0;

-- soft delete된 room도 처리
UPDATE room r
JOIN house h ON h.user_id = r.user_id
SET r.house_id = h.house_id
WHERE r.is_deleted = 1;

-- ============================================================
-- STEP 5. house_id NOT NULL 제약 추가
-- ============================================================
ALTER TABLE room
    MODIFY COLUMN house_id BIGINT NOT NULL;

-- ============================================================
-- STEP 6. users 테이블에 main_house_id 컬럼 추가
-- ============================================================
ALTER TABLE user
    ADD COLUMN main_house_id BIGINT NULL AFTER main_room_id;

-- ============================================================
-- STEP 7. users.main_house_id 설정
--         기존 main_room_id가 속한 house를 대표 집으로 지정
-- ============================================================
UPDATE user u
JOIN room r ON r.room_id = u.main_room_id AND r.is_deleted = 0
JOIN house h ON h.house_id = r.house_id
SET u.main_house_id = h.house_id
WHERE u.main_room_id IS NOT NULL
  AND u.is_deleted = 0;

-- main_room_id가 없는 유저 중 집이 있으면 첫 번째 집을 대표로
UPDATE user u
JOIN house h ON h.user_id = u.id
SET u.main_house_id = h.house_id
WHERE u.main_house_id IS NULL
  AND u.is_deleted = 0;

-- ============================================================
-- STEP 8. 기존 main_room_id 컬럼 제거 (선택사항)
--         롤백 가능성을 위해 일단 주석 처리
--         검증 완료 후 수동으로 실행 권장
-- ============================================================
-- ALTER TABLE user DROP COLUMN main_room_id;
-- ALTER TABLE room DROP COLUMN user_id;

-- ============================================================
-- 검증 쿼리 (COMMIT 전 확인용)
-- ============================================================
-- SELECT COUNT(*) FROM room WHERE house_id IS NULL;      -- 0이어야 함
-- SELECT COUNT(*) FROM house;                            -- 유저 수와 동일해야 함
-- SELECT COUNT(*) FROM user WHERE main_house_id IS NULL AND is_deleted = 0;  -- 0이 이상적

COMMIT;
