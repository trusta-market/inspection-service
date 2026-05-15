package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

public record CarrierReturnCompletedEvent(
        UUID productId
) {
}
