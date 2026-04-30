package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteInspectionCommand;

public interface CompleteInspectionUseCase {

    void complete(CompleteInspectionCommand command);
}
