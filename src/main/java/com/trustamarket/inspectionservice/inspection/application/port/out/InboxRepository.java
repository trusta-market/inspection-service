package com.trustamarket.inspectionservice.inspection.application.port.out;

import java.util.UUID;

// Kafka 소비 멱등성 port — application 레이어가 persistence(Inbox JPA)에 직접 의존하지 않게 추상화.
//
// 동일 트랜잭션 dedup: isAlreadyProcessed(SELECT) → 도메인 처리 → record(INSERT)를 한 트랜잭션으로 묶는다.
// 같은 eventId는 같은 파티션(productId key)→같은 스레드로 순차 처리되므로 exists-check에 race가 없다.
public interface InboxRepository {

    // (eventId, consumerGroup)가 이미 기록돼 있으면 true — 중복 메시지.
    boolean isAlreadyProcessed(UUID eventId, String consumerGroup);

    // 처리 완료를 기록. 도메인 작업과 같은 트랜잭션에서 커밋돼야 유실/중복이 모두 0.
    void record(UUID eventId, String consumerGroup, InboxPurpose purpose);
}
