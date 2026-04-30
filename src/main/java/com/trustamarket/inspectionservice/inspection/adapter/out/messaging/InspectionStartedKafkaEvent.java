package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import java.util.UUID;

public record InspectionStartedKafkaEvent(
        UUID inspectionId,
        UUID productId
) {
}
