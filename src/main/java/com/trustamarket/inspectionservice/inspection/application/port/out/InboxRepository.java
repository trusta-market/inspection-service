package com.trustamarket.inspectionservice.inspection.application.port.out;

import java.util.UUID;

public interface InboxRepository {

    boolean isAlreadyProcessed(UUID eventId, String consumerGroup);

    void record(UUID eventId, String consumerGroup, InboxPurpose purpose);
}
