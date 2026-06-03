package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

public record InspectionPriceAcceptedEvent(
        UUID eventId,
        UUID productId,
        UUID sellerId,
        Long finalPrice
) {
}
