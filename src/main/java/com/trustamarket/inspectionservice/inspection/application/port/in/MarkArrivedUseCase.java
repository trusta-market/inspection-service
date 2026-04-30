package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;

public interface MarkArrivedUseCase {
    void markArrived(MarkArrivedCommand command);
}
