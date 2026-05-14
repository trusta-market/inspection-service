package com.trustamarket.inspectionservice.inspection.application.event;

import java.util.UUID;

public record PricingCompletedEvent(
        UUID inspectionId,
        UUID productId,
        String grade,
        long suggestedPriceAmount,
        String currency
) {
}
