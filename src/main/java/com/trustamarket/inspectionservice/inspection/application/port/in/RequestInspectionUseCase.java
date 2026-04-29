package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;

public interface RequestInspectionUseCase {
    void request(RequestInspectionCommand command);
}
