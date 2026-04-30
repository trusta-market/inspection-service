package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionSummaryResult;

import java.time.Instant;
import java.util.UUID;

public record GetInspectionSummaryResponse(
        UUID inspectionId,
        UUID productId,
        UUID centerId,
        long originalPriceAmount,
        String originalPriceCurrency,
        String status,
        Instant requestedAt
) {
    public static GetInspectionSummaryResponse from(GetInspectionSummaryResult result) {
        return new GetInspectionSummaryResponse(
                result.inspectionId(),
                result.productId(),
                result.centerId(),
                result.originalPriceAmount(),
                result.originalPriceCurrency(),
                result.status().name(),
                result.requestedAt()
        );
    }
}
