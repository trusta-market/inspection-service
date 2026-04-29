package com.trustamarket.inspectionservice.center.application.port.in;

import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;

public interface AssignCenterForInspectionUseCase {
    void assign(AssignCenterCommand command);
}
