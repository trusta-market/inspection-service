package com.trustamarket.inspectionservice.inspection.application.dto.command;

import java.util.Map;
import java.util.UUID;

public record CompleteInspectionCommand(
        UUID inspectionId,
        String grade,
        long suggestedPriceAmount,
        String currency,
        String inspectorNote,
        Map<String, Object> resultDetail
) {
}
