package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request;

import com.trustamarket.inspectionservice.inspection.application.dto.command.StartInspectionCommand;

import java.util.UUID;

public record StartInspectionRequest(UUID inspectorId) {

    public StartInspectionCommand toCommand(UUID inspectionId) {
        return new StartInspectionCommand(inspectionId, inspectorId);
    }
}
