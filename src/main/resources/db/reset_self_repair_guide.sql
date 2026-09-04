-- 자가 수리 안내 캐시 전체 삭제 (하자 데이터는 유지)
--
-- 목적: 자가 수리 가능 여부 판정을 룰(SelfRepairPolicy)에서 모델 판단으로 바꿨기 때문에,
--       옛 룰로 만들어져 저장된 안내를 비워 다음 조회 때 새 기준으로 다시 생성되게 한다.
--
-- 지우는 것 : defect_repair_guide 전체
-- 남기는 것 : defect, analysis, scan 등 나머지 전부
--
-- 삭제 후 하자 목록을 다시 조회하면 prefetch가 안내를 새로 만든다.

START TRANSACTION;

DELETE FROM defect_repair_guide;

COMMIT;

-- 확인용 (0이어야 한다)
SELECT 'defect_repair_guide' AS t, COUNT(*) AS cnt FROM defect_repair_guide
UNION ALL SELECT 'defect(유지)', COUNT(*) FROM defect;
