package com.trustamarket.inspectionservice.center.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;

import java.util.UUID;

public record RegisterCenterResponse(
        UUID centerId,
        String name,
        String status
) {
    public static RegisterCenterResponse from(RegisterCenterResult result) {
        return new RegisterCenterResponse(result.centerId(), result.name(), result.status().name());
    }
}
