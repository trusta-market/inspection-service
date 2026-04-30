package com.trustamarket.inspectionservice.center.application.port.in;

import com.trustamarket.inspectionservice.center.application.dto.result.ChangeCenterStatusResult;

import java.util.UUID;

public interface ChangeCenterStatusUseCase {

    ChangeCenterStatusResult open(UUID centerId);

    ChangeCenterStatusResult startMaintenance(UUID centerId);

    ChangeCenterStatusResult close(UUID centerId);
}
