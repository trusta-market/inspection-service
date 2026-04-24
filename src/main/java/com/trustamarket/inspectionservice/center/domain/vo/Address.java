package com.trustamarket.inspectionservice.center.domain.vo;

import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;

public record Address(
        String line1,
        String line2,
        String city,
        String postalCode
) {
    public Address {
        if (line1 == null || line1.isBlank()) {
            throw new InspectionCenterException("주소(line1)는 비어있을 수 없습니다");
        }
    }
}
