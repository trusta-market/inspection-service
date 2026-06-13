--liquibase formatted sql

--changeset Seungwon-Choi:5 runInTransaction:false
CREATE INDEX IF NOT EXISTS idx_inspections_seller_active
    ON p_inspections (seller_id)
    WHERE deleted_at IS NULL;
