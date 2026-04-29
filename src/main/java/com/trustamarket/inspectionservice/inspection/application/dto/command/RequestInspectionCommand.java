package com.trustamarket.inspectionservice.inspection.application.dto.command;

import java.util.UUID;

public record RequestInspectionCommand(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {}
