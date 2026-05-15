package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record SellerId(UUID value) {
    public SellerId {
        if (value == null) {
            throw new InspectionException(InspectionErrorCode.INVALID_SELLER_ID);
        }
    }

    public static SellerId of(UUID value) {
        return new SellerId(value);
    }
}
