package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.StartInspectionCommand;

public interface StartInspectionUseCase {

    void start(StartInspectionCommand command);
}
