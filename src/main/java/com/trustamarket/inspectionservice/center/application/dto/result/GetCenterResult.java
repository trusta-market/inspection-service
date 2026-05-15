package com.trustamarket.inspectionservice.center.application.dto.result;

import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;

import java.util.UUID;

public record GetCenterResult(
        UUID centerId,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String contactPhone,
        CenterStatus status
) {
    public static GetCenterResult from(InspectionCenter center) {
        return new GetCenterResult(
                center.getId().value(),
                center.getName(),
                center.getAddress().line1(),
                center.getAddress().line2(),
                center.getAddress().city(),
                center.getAddress().postalCode(),
                center.getContactPhone(),
                center.getStatus()
        );
    }
}
