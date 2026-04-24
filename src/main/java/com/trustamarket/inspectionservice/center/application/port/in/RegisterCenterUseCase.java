package com.trustamarket.inspectionservice.center.application.port.in;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;

public interface RegisterCenterUseCase {

    RegisterCenterResult register(RegisterCenterCommand command);
}
