package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

public record DeliveryCompletedEvent(
        UUID productId
) {
}
