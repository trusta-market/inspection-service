package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record InspectorId(UUID value) {
    public InspectorId {
        if (value == null) {
            throw new InspectionException("InspectorId는 null일 수 없습니다");
        }
    }

    public static InspectorId of(UUID value) {
        return new InspectorId(value);
    }
}
