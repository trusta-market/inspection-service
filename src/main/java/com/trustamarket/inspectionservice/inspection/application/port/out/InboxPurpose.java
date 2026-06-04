package com.trustamarket.inspectionservice.inspection.application.port.out;

// inbox row가 어떤 Kafka 소비 흐름의 멱등성 기록인지 — 관측/디버깅용. dedup 키는 (event_id, consumer_group).
public enum InboxPurpose {
    INSPECTION_REQUESTED,
    INSPECTION_PRICE_ACCEPTED,
    INSPECTION_PRICE_REJECTED,
    CARRIER_DELIVERY_COMPLETED,
    CARRIER_RETURN_COMPLETED
}
