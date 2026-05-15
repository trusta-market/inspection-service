package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.FailInspectionCommand;

public interface FailInspectionUseCase {

    void fail(FailInspectionCommand command);
}
