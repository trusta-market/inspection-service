package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record PhotoId(UUID value) {
    public PhotoId {
        if (value == null) {
            throw new InspectionException(InspectionErrorCode.INVALID_PHOTO_ID);
        }
    }

    public static PhotoId generate() {
        return new PhotoId(UUID.randomUUID());
    }

    public static PhotoId of(UUID value) {
        return new PhotoId(value);
    }
}
