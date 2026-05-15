package com.trustamarket.inspectionservice.center.domain.vo;

import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterErrorCode;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;

public record Address(
        String line1,
        String line2,
        String city,
        String postalCode
) {
    public Address {
        if (line1 == null || line1.isBlank()) {
            throw new InspectionCenterException(InspectionCenterErrorCode.INVALID_ADDRESS_LINE1);
        }
        if (city == null || city.isBlank()) {
            throw new InspectionCenterException(InspectionCenterErrorCode.INVALID_CITY);
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new InspectionCenterException(InspectionCenterErrorCode.INVALID_POSTAL_CODE);
        }
    }
}
