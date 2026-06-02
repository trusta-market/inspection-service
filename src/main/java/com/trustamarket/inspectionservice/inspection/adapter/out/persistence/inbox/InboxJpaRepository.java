package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.inbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<InboxJpaEntity, UUID> {

    boolean existsByEventIdAndConsumerGroup(UUID eventId, String consumerGroup);
}
