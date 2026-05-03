package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        if (value == null) {
            throw new InspectionException(InspectionErrorCode.INVALID_PRODUCT_ID);
        }
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }
}
