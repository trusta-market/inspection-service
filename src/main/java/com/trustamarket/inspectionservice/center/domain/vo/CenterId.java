package com.trustamarket.inspectionservice.center.domain.vo;

import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterErrorCode;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;

import java.util.UUID;

public record CenterId(UUID value) {
    public CenterId {
        if (value == null) {
            throw new InspectionCenterException(InspectionCenterErrorCode.INVALID_CENTER_ID);
        }
    }

    public static CenterId generate() {
        return new CenterId(UUID.randomUUID());
    }

    public static CenterId of(UUID value) {
        return new CenterId(value);
    }
}
