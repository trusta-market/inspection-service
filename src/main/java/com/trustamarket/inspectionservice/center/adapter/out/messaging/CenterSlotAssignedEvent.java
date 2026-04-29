package com.trustamarket.inspectionservice.center.adapter.out.messaging;

import java.util.UUID;

public record CenterSlotAssignedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {}
