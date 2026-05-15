package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record InspectorId(UUID value) {
    public InspectorId {
        if (value == null) {
            throw new InspectionException(InspectionErrorCode.INVALID_INSPECTOR_ID);
        }
    }

    public static InspectorId of(UUID value) {
        return new InspectorId(value);
    }
}
