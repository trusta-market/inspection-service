package com.trustamarket.inspectionservice.center.domain.vo;

import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;

import java.util.UUID;

public record CenterId(UUID value) {
    public CenterId {
        if (value == null) {
            throw new InspectionCenterException("CenterId는 null일 수 없습니다");
        }
    }

    public static CenterId generate() {
        return new CenterId(UUID.randomUUID());
    }

    public static CenterId of(UUID value) {
        return new CenterId(value);
    }
}
