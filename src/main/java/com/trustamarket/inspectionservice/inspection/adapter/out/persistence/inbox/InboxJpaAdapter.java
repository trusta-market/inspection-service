package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.inbox;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// InboxRepository의 JPA 구현. 트랜잭션 경계는 호출자(consumer 리스너)가 잡으며,
// 여기서 별도 @Transactional을 두지 않아 도메인 처리와 같은 트랜잭션에 참여한다.
@Repository
@RequiredArgsConstructor
public class InboxJpaAdapter implements InboxRepository {

    private final InboxJpaRepository inboxJpaRepository;

    @Override
    public boolean isAlreadyProcessed(UUID eventId, String consumerGroup) {
        return inboxJpaRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup);
    }

    @Override
    public void record(UUID eventId, String consumerGroup, InboxPurpose purpose) {
        inboxJpaRepository.save(InboxJpaEntity.forKafkaEvent(eventId, consumerGroup, purpose));
    }
}
