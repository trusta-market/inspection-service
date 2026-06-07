package com.trustamarket.inspectionservice.inspection.application.dto.result;

import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;

import java.time.Instant;
import java.util.UUID;

public record GetInspectionSummaryResult(
        UUID inspectionId,
        UUID productId,
        UUID centerId,
        long originalPriceAmount,
        String originalPriceCurrency,
        InspectionStatus status,
        Instant requestedAt
) {
}
