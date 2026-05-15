package com.trustamarket.inspectionservice.inspection.application.event;

import java.util.UUID;

public record InspectionReturnCompletedEvent(
        UUID inspectionId,
        UUID productId,
        UUID sellerId
) {
}
