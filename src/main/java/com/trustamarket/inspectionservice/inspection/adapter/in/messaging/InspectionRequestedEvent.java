package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

record InspectionRequestedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {
}
