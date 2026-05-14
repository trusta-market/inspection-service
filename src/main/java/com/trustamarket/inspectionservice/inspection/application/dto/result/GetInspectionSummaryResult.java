package com.trustamarket.inspectionservice.inspection.application.dto.result;

import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;

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
    public static GetInspectionSummaryResult from(Inspection inspection) {
        return new GetInspectionSummaryResult(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getCenterId().value(),
                inspection.getOriginalPrice().amount().longValue(),
                inspection.getOriginalPrice().currency().name(),
                inspection.getStatus(),
                inspection.getRequestedAt()
        );
    }
}
