package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.inbox;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// p_inspection_inboxes — Kafka 메시지 멱등성 기록.
// (event_id, consumer_group) UNIQUE — at-least-once 재배달 시 동일 메시지 중복 처리 차단.
@Entity
@Table(name = "p_inspection_inboxes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InboxJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "consumer_group", length = 50, nullable = false, updatable = false)
    private String consumerGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 30, nullable = false, updatable = false)
    private InboxPurpose purpose;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private InboxJpaEntity(UUID id, UUID eventId, String consumerGroup, InboxPurpose purpose, Instant createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.purpose = purpose;
        this.createdAt = createdAt;
    }

    public static InboxJpaEntity forKafkaEvent(UUID eventId, String consumerGroup, InboxPurpose purpose) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(purpose, "purpose must not be null");
        if (consumerGroup == null || consumerGroup.isBlank()) {
            throw new IllegalArgumentException("consumerGroup must not be blank");
        }
        return new InboxJpaEntity(UUID.randomUUID(), eventId, consumerGroup, purpose, Instant.now());
    }
}
