--liquibase formatted sql

--changeset Seungwon-Choi:1
CREATE TABLE IF NOT EXISTS p_inspection_centers (
    center_id     UUID         PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city          VARCHAR(255) NOT NULL,
    postal_code   VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(255),
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    deleted_at    TIMESTAMPTZ,
    deleted_by    VARCHAR(255),
    CONSTRAINT p_inspection_centers_status_check CHECK (status IN ('OPEN', 'MAINTENANCE', 'CLOSED'))
);

--changeset Seungwon-Choi:2
CREATE TABLE IF NOT EXISTS p_inspections (
    inspection_id            UUID           PRIMARY KEY,
    product_id               UUID           NOT NULL,
    seller_id                UUID           NOT NULL,
    center_id                UUID           NOT NULL,
    original_price_amount    NUMERIC(19, 2) NOT NULL,
    original_price_currency  VARCHAR(50)    NOT NULL,
    status                   VARCHAR(50)    NOT NULL,
    inspector_id             UUID,
    requested_at             TIMESTAMPTZ    NOT NULL,
    arrived_at               TIMESTAMPTZ,
    started_at               TIMESTAMPTZ,
    inspection_done_at       TIMESTAMPTZ,
    priced_at                TIMESTAMPTZ,
    return_completed_at      TIMESTAMPTZ,
    grade                    VARCHAR(10),
    suggested_price_amount   NUMERIC(19, 2),
    suggested_price_currency VARCHAR(50),
    inspector_note           TEXT,
    result_detail            TEXT,
    created_at               TIMESTAMP      NOT NULL,
    updated_at               TIMESTAMP      NOT NULL,
    deleted_at               TIMESTAMP,
    deleted_by               VARCHAR(255)
);

--changeset Seungwon-Choi:3
CREATE TABLE IF NOT EXISTS p_inspection_photos (
    photo_id      UUID        PRIMARY KEY,
    inspection_id UUID        NOT NULL,
    type          VARCHAR(50) NOT NULL,
    url           VARCHAR(255) NOT NULL,
    caption       VARCHAR(255),
    display_order INTEGER     NOT NULL,
    created_at    TIMESTAMP   NOT NULL,
    CONSTRAINT fk_inspection_photos_inspection FOREIGN KEY (inspection_id) REFERENCES p_inspections (inspection_id)
);
