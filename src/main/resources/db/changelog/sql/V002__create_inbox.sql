--liquibase formatted sql

--changeset Seungwon-Choi:4
CREATE TABLE IF NOT EXISTS p_inspection_inboxes (
    id             UUID         PRIMARY KEY,
    event_id       UUID         NOT NULL,
    consumer_group VARCHAR(50)  NOT NULL,
    purpose        VARCHAR(30)  NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uq_p_inspection_inboxes_event_id_consumer_group UNIQUE (event_id, consumer_group)
);
