package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetCenterIdByProductIdResult;

import java.util.UUID;

public record GetCenterIdByProductIdResponse(UUID centerId) {

    public static GetCenterIdByProductIdResponse from(GetCenterIdByProductIdResult result) {
        return new GetCenterIdByProductIdResponse(result.centerId());
    }
}
