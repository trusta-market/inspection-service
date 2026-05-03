package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record InspectionId(UUID value) {
    public InspectionId {
        if (value == null) {
            throw new InspectionException(InspectionErrorCode.INVALID_INSPECTION_ID);
        }
    }

    public static InspectionId generate() {
        return new InspectionId(UUID.randomUUID());
    }

    public static InspectionId of(UUID value) {
        return new InspectionId(value);
    }
}
