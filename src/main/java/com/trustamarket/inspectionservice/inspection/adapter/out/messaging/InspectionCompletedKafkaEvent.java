package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import java.util.UUID;

public record InspectionCompletedKafkaEvent(
        UUID inspectionId,
        UUID productId
) {
}
