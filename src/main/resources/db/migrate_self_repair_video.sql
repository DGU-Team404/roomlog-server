-- 자가 수리 안내: 검색 결과 링크 → 실제 영상 2건 + 준비물 정보로 변경

-- 영상 목록(JSON)과 재검색용 검색어 컬럼 추가
ALTER TABLE defect_repair_guide ADD COLUMN videos TEXT NULL;
ALTER TABLE defect_repair_guide ADD COLUMN video_search_query VARCHAR(255) NULL;

-- 기존 안내는 items JSON 구조와 영상 형식이 모두 달라 그대로 쓸 수 없다.
-- 삭제하면 다음 조회 시 새 형식으로 다시 생성된다.
DELETE FROM defect_repair_guide;

-- 더 이상 쓰지 않는 단일 영상 컬럼 제거
ALTER TABLE defect_repair_guide DROP COLUMN video_url;
ALTER TABLE defect_repair_guide DROP COLUMN video_title;
ALTER TABLE defect_repair_guide DROP COLUMN video_thumbnail_url;
ALTER TABLE defect_repair_guide DROP COLUMN video_channel;
