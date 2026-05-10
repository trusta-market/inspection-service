package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import java.util.UUID;

public record ProductInspectionRequestedEvent(
        UUID productId,
        UUID sellerId,
        long originalPrice,
        String currency,
        String inspectionType
) {}
