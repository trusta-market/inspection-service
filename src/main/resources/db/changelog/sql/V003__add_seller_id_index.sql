--liquibase formatted sql

--changeset seungwon:5
-- cycle-2 개선(단일 변수): /me 조회의 count+list가 seller_id Seq Scan을 매 호출 수행 → 풀 점유.
-- 두 쿼리 술어가 모두 `seller_id=? AND deleted_at IS NULL`이라 살아있는 행만 부분 인덱싱한다.
CREATE INDEX IF NOT EXISTS idx_inspections_seller_active
    ON p_inspections (seller_id)
    WHERE deleted_at IS NULL;
