package com.trustamarket.inspectionservice.inspection.application.dto.command;

import java.util.Map;
import java.util.UUID;

public record FailInspectionCommand(
        UUID inspectionId,
        String inspectorNote,
        Map<String, Object> resultDetail
) {
}
