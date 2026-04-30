package com.trustamarket.inspectionservice.inspection.application.event;

import java.util.UUID;

public record InspectionStartedEvent(
        UUID inspectionId,
        UUID productId
) {
}
