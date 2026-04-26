package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        if (value == null) {
            throw new InspectionException("ProductId는 null일 수 없습니다");
        }
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }
}
