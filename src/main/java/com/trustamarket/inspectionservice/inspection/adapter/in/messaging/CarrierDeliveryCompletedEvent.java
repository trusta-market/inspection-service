package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import java.util.UUID;

record CarrierDeliveryCompletedEvent(
        UUID productId
) {
}
