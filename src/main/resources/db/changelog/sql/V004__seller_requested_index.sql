--liquibase formatted sql

--changeset seungwon:6 runInTransaction:false
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_inspections_seller_requested
    ON p_inspections (seller_id, requested_at DESC)
    WHERE deleted_at IS NULL;

--changeset seungwon:7 runInTransaction:false
DROP INDEX CONCURRENTLY IF EXISTS idx_inspections_seller_active;
