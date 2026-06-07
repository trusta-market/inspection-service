package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.inbox;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
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
        Objects.requireNonNull(eventId, "eventId는 null일 수 없습니다");
        Objects.requireNonNull(purpose, "purpose는 null일 수 없습니다");
        if (consumerGroup == null || consumerGroup.isBlank()) {
            throw new InspectionException(InspectionErrorCode.INVALID_CONSUMER_GROUP);
        }
        return new InboxJpaEntity(UUID.randomUUID(), eventId, consumerGroup, purpose, Instant.now());
    }
}
