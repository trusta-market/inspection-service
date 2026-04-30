package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import java.util.UUID;

public record PricingCompletedKafkaEvent(
        UUID inspectionId,
        UUID productId,
        String grade,
        long suggestedPriceAmount,
        String currency
) {
}
