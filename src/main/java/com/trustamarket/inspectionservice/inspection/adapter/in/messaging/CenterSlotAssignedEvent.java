package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

public record CenterSlotAssignedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {}
