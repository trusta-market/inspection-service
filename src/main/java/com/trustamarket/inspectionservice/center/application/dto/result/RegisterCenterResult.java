package com.trustamarket.inspectionservice.center.application.dto.result;

import com.trustamarket.inspectionservice.center.domain.model.enums.CenterStatus;

import java.util.UUID;

public record RegisterCenterResult(
        UUID centerId,
        String name,
        CenterStatus status
) {}
