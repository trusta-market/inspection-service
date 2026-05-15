package com.trustamarket.inspectionservice.inspection.application.dto.command;

import java.util.UUID;

public record StartInspectionCommand(
        UUID inspectionId,
        UUID inspectorId
) {
    public static StartInspectionCommand of(UUID inspectionId, UUID inspectorId) {
        return new StartInspectionCommand(inspectionId, inspectorId);
    }
}
