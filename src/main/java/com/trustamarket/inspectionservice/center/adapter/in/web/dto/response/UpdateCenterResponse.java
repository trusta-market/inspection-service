package com.trustamarket.inspectionservice.center.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.center.application.dto.result.UpdateCenterResult;

import java.util.UUID;

public record UpdateCenterResponse(
        UUID centerId,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String contactPhone,
        int capacity,
        String status
) {
    public static UpdateCenterResponse from(UpdateCenterResult result) {
        return new UpdateCenterResponse(
                result.centerId(),
                result.name(),
                result.addressLine1(),
                result.addressLine2(),
                result.city(),
                result.postalCode(),
                result.contactPhone(),
                result.capacity(),
                result.status().name()
        );
    }
}
