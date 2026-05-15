package com.trustamarket.inspectionservice.inspection.application.event;

import java.util.UUID;

public record InspectionFailedEvent(
        UUID inspectionId,
        UUID productId,
        UUID sellerId
) {
}
