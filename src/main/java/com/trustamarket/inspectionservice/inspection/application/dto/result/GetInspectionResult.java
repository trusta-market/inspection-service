package com.trustamarket.inspectionservice.inspection.application.dto.result;

import com.trustamarket.inspectionservice.inspection.domain.enums.Grade;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.model.InspectionPhoto;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetInspectionResult(
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
        InspectionStatus status,
        Grade grade,
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
    public static GetInspectionResult from(Inspection inspection) {
        return new GetInspectionResult(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getSellerId().value(),
                inspection.getCenterId().value(),
                inspection.getInspectorId() != null ? inspection.getInspectorId().value() : null,
                inspection.getOriginalPrice().amount().longValue(),
                inspection.getOriginalPrice().currency().name(),
                inspection.getSuggestedPrice() != null ? inspection.getSuggestedPrice().amount().longValue() : null,
                inspection.getSuggestedPrice() != null ? inspection.getSuggestedPrice().currency().name() : null,
                inspection.getFinalPrice() != null ? inspection.getFinalPrice().amount().longValue() : null,
                inspection.getFinalPrice() != null ? inspection.getFinalPrice().currency().name() : null,
                inspection.getStatus(),
                inspection.getGrade(),
                inspection.getInspectorNote(),
                inspection.getRejectReason(),
                inspection.getResultDetail(),
                inspection.getRequestedAt(),
                inspection.getArrivedAt(),
                inspection.getStartedAt(),
                inspection.getInspectionDoneAt(),
                inspection.getPricedAt(),
                inspection.getSellerDecidedAt(),
                inspection.getPhotos()
        );
    }
}
