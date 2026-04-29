package com.trustamarket.inspectionservice.center.application.dto.command;

import java.util.UUID;

public record AssignCenterCommand(
        UUID productId,
        UUID sellerId,
        long originalPriceAmount,
        String currency
) {
}
