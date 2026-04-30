package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request;

import com.trustamarket.inspectionservice.inspection.application.dto.command.FailInspectionCommand;

import java.util.Map;
import java.util.UUID;

public record FailInspectionRequest(
        String inspectorNote,
        Map<String, Object> resultDetail
) {
    public FailInspectionCommand toCommand(UUID inspectionId) {
        return new FailInspectionCommand(inspectionId, inspectorNote, resultDetail);
    }
}
