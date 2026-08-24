-- 자가 수리 안내: 검색 결과 링크 → 실제 영상/상품 1건으로 변경
-- 영상 정보 컬럼 추가
ALTER TABLE defect_repair_guide ADD COLUMN video_title VARCHAR(255) NULL;
ALTER TABLE defect_repair_guide ADD COLUMN video_thumbnail_url TEXT NULL;
ALTER TABLE defect_repair_guide ADD COLUMN video_channel VARCHAR(255) NULL;
ALTER TABLE defect_repair_guide ADD COLUMN video_search_query VARCHAR(255) NULL;

-- 기존 안내는 items JSON 구조가 달라 그대로 쓸 수 없다.
-- 삭제하면 다음 조회 시 새 형식으로 다시 생성된다.
DELETE FROM defect_repair_guide;
