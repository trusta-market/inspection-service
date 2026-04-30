package com.trustamarket.inspectionservice.center.application.dto.result;

import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;

import java.util.UUID;

public record ChangeCenterStatusResult(UUID centerId, CenterStatus status) {
}
