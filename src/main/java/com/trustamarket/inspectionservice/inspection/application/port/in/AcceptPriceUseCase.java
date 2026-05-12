package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.AcceptPriceCommand;

public interface AcceptPriceUseCase {
    void accept(AcceptPriceCommand command);
}
