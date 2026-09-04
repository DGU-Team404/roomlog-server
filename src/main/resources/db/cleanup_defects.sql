-- 하자 점검 결과 전체 삭제 (스캔 데이터는 유지)
--
-- 목적: scan 테이블의 zip/ply 데이터를 그대로 두고, 그 위에서 만들어진
--       하자 탐지 결과만 비워서 처음부터 다시 탐지할 수 있게 한다.
--
-- 지우는 것 : analysis, defect, defect_repair_guide,
--             estimate, estimate_defect, repair, repair_defect,
--             하자 상담 chat_message
-- 남기는 것 : scan, room, house, user, refresh_token,
--             defect_unit_price, repair_supply, chat_answer_cache,
--             chat_session, 일반 chat_message

START TRANSACTION;

-- 하자 상담 대화 (defect_id가 실린 메시지만)
DELETE FROM chat_message WHERE defect_id IS NOT NULL;

-- 견적/수리 (analysis, defect를 참조)
DELETE FROM repair_defect;
DELETE FROM repair;
DELETE FROM estimate_defect;
DELETE FROM estimate;

-- 자가 수리 안내 캐시 (defect_id가 PK)
DELETE FROM defect_repair_guide;

-- 하자 및 탐지 이력
DELETE FROM defect;
DELETE FROM analysis;

-- AUTO_INCREMENT 초기화 (id를 1부터 다시 시작)
ALTER TABLE chat_message      AUTO_INCREMENT = 1;
ALTER TABLE repair_defect     AUTO_INCREMENT = 1;
ALTER TABLE repair            AUTO_INCREMENT = 1;
ALTER TABLE estimate_defect   AUTO_INCREMENT = 1;
ALTER TABLE estimate          AUTO_INCREMENT = 1;
ALTER TABLE defect            AUTO_INCREMENT = 1;
ALTER TABLE analysis          AUTO_INCREMENT = 1;

COMMIT;

-- 확인용
SELECT 'scan(유지)' AS t, COUNT(*) AS cnt FROM scan
UNION ALL SELECT 'room(유지)', COUNT(*) FROM room
UNION ALL SELECT 'analysis', COUNT(*) FROM analysis
UNION ALL SELECT 'defect', COUNT(*) FROM defect
UNION ALL SELECT 'defect_repair_guide', COUNT(*) FROM defect_repair_guide
UNION ALL SELECT 'estimate', COUNT(*) FROM estimate
UNION ALL SELECT 'estimate_defect', COUNT(*) FROM estimate_defect
UNION ALL SELECT 'repair', COUNT(*) FROM repair
UNION ALL SELECT 'repair_defect', COUNT(*) FROM repair_defect;
