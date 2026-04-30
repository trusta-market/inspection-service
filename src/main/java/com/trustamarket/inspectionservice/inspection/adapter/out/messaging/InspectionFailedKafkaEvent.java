package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import java.util.UUID;

public record InspectionFailedKafkaEvent(
        UUID inspectionId,
        UUID productId,
        UUID sellerId
) {
}
