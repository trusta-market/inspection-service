package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.RejectPriceCommand;

public interface RejectPriceUseCase {

    void reject(RejectPriceCommand command);
}
