package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import java.util.UUID;

public record InspectionReturnCompletedKafkaEvent(
        UUID inspectionId,
        UUID productId,
        UUID sellerId
) {
}
