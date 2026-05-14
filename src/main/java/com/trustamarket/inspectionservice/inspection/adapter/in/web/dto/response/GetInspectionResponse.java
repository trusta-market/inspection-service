package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionResult;
import com.trustamarket.inspectionservice.inspection.domain.model.InspectionPhoto;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetInspectionResponse(
        UUID inspectionId,
        UUID productId,
        UUID sellerId,
        UUID centerId,
        UUID inspectorId,
        long originalPriceAmount,
        String originalPriceCurrency,
        Long suggestedPriceAmount,
        String suggestedPriceCurrency,
        Long finalPriceAmount,
        String finalPriceCurrency,
        String status,
        String grade,
        String inspectorNote,
        String rejectReason,
        InspectionResultDetail resultDetail,
        Instant requestedAt,
        Instant arrivedAt,
        Instant startedAt,
        Instant inspectionDoneAt,
        Instant pricedAt,
        Instant sellerDecidedAt,
        List<InspectionPhoto> photos
) {
    public static GetInspectionResponse from(GetInspectionResult result) {
        return new GetInspectionResponse(
                result.inspectionId(),
                result.productId(),
                result.sellerId(),
                result.centerId(),
                result.inspectorId(),
                result.originalPriceAmount(),
                result.originalPriceCurrency(),
                result.suggestedPriceAmount(),
                result.suggestedPriceCurrency(),
                result.finalPriceAmount(),
                result.finalPriceCurrency(),
                result.status().name(),
                result.grade() != null ? result.grade().name() : null,
                result.inspectorNote(),
                result.rejectReason(),
                result.resultDetail(),
                result.requestedAt(),
                result.arrivedAt(),
                result.startedAt(),
                result.inspectionDoneAt(),
                result.pricedAt(),
                result.sellerDecidedAt(),
                result.photos()
        );
    }
}
