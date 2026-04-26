package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record InspectionId(UUID value) {
    public InspectionId {
        if (value == null) {
            throw new InspectionException("InspectionId는 null일 수 없습니다");
        }
    }

    public static InspectionId generate() {
        return new InspectionId(UUID.randomUUID());
    }

    public static InspectionId of(UUID value) {
        return new InspectionId(value);
    }
}
