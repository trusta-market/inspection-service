package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.inbox;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
