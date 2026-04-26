package com.trustamarket.inspectionservice.inspection.domain.vo;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;

import java.util.UUID;

public record SellerId(UUID value) {
    public SellerId {
        if (value == null) {
            throw new InspectionException("SellerId는 null일 수 없습니다");
        }
    }

    public static SellerId of(UUID value) {
        return new SellerId(value);
    }
}
