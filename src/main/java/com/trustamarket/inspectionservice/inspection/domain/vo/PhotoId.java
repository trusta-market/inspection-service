package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record PhotoId(UUID value) {
    public PhotoId {
        if (value == null) {
            throw new InspectionException("PhotoId는 null일 수 없습니다");
        }
    }

    public static PhotoId generate() {
        return new PhotoId(UUID.randomUUID());
    }

    public static PhotoId of(UUID value) {
        return new PhotoId(value);
    }
}
