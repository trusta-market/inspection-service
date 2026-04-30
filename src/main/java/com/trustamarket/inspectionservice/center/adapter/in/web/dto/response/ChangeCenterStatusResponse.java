package com.trustamarket.inspectionservice.center.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.center.application.dto.result.ChangeCenterStatusResult;

import java.util.UUID;

public record ChangeCenterStatusResponse(UUID centerId, String status) {

    public static ChangeCenterStatusResponse from(ChangeCenterStatusResult result) {
        return new ChangeCenterStatusResponse(result.centerId(), result.status().name());
    }
}
