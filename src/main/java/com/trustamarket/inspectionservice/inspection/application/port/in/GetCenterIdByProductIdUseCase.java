package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetCenterIdByProductIdResult;

import java.util.UUID;

public interface GetCenterIdByProductIdUseCase {

    GetCenterIdByProductIdResult getCenterId(UUID productId);
}
