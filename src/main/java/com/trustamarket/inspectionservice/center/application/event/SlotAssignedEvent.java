package com.trustamarket.inspectionservice.center.application.event;

import java.util.UUID;

public record SlotAssignedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {}
