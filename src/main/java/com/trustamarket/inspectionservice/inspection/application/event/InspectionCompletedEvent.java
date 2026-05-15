package com.trustamarket.inspectionservice.inspection.application.event;

import java.util.UUID;

public record InspectionCompletedEvent(
        UUID inspectionId,
        UUID productId,
        String grade,
        long suggestedPriceAmount,
        String currency,
        UUID inspectorId
) {
}
