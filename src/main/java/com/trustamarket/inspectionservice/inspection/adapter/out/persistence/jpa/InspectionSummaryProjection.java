package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InspectionSummaryProjection(
        UUID inspectionId,
        UUID productId,
        UUID centerId,
        BigDecimal originalPriceAmount,
        CurrencyCode originalPriceCurrency,
        InspectionStatus status,
        Instant requestedAt
) {
}
