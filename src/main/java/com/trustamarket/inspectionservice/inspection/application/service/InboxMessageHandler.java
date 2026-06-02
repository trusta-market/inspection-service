package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Kafka 소비 멱등성 오케스트레이션. dedup(SELECT) + 도메인 처리 + inbox 기록(INSERT)을 한 트랜잭션으로 묶는다.
// 트랜잭션 경계를 application 계층에 두어, 리스너(adapter)는 반환 후 동기 ack만 하면 된다 — 반환 시점엔 이미 커밋됐으므로.
// 같은 eventId는 같은 파티션(productId key)→같은 스레드로 순차 처리되므로 isAlreadyProcessed에 race가 없다.
@Service
@RequiredArgsConstructor
public class InboxMessageHandler {

    private final InboxRepository inboxRepository;

    // 처음 보는 이벤트면 domainWork 실행 후 inbox 기록하고 true, 이미 처리한 중복이면 domainWork 없이 false.
    @Transactional
    public boolean process(UUID eventId, String consumerGroup, InboxPurpose purpose, Runnable domainWork) {
        if (inboxRepository.isAlreadyProcessed(eventId, consumerGroup)) {
            return false;
        }
        domainWork.run();
        inboxRepository.record(eventId, consumerGroup, purpose);
        return true;
    }
}
