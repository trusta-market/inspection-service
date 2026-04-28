package com.trustamarket.inspectionservice.center.application.port.in;

import com.trustamarket.inspectionservice.center.application.dto.command.UpdateCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.UpdateCenterResult;

public interface UpdateCenterUseCase {

    UpdateCenterResult updateCenter(UpdateCenterCommand command);
}
