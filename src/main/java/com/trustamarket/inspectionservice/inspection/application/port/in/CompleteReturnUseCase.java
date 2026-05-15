package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;

public interface CompleteReturnUseCase {

    void completeReturn(CompleteReturnCommand command);
}
