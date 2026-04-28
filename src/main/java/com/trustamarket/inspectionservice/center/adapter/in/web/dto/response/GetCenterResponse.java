package com.trustamarket.inspectionservice.center.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterResult;

import java.util.UUID;

public record GetCenterResponse(
        UUID centerId,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String contactPhone,
        int capacity,
        int currentLoad,
        String status
) {
    public static GetCenterResponse from(GetCenterResult result) {
        return new GetCenterResponse(
                result.centerId(),
                result.name(),
                result.addressLine1(),
                result.addressLine2(),
                result.city(),
                result.postalCode(),
                result.contactPhone(),
                result.capacity(),
                result.currentLoad(),
                result.status().name()
        );
    }
}
