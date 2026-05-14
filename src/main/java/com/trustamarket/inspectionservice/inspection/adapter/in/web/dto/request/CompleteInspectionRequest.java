package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request;

import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteInspectionCommand;

import java.util.Map;
import java.util.UUID;

public record CompleteInspectionRequest(
        String grade,
        long suggestedPriceAmount,
        String currency,
        String inspectorNote,
        Map<String, Object> resultDetail
) {
    public CompleteInspectionCommand toCommand(UUID inspectionId) {
        return new CompleteInspectionCommand(
                inspectionId, grade, suggestedPriceAmount, currency, inspectorNote, resultDetail
        );
    }
}
