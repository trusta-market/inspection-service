package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

public record InspectionPriceRejectedEvent(
        UUID eventId,
        UUID productId,
        UUID sellerId,
        String reason
) {
}
