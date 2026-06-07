package com.trustamarket.inspectionservice.center.domain.exception;

import com.trustamarket.common.exception.CustomException;
import lombok.Getter;

@Getter
public class InspectionCenterException extends CustomException {

    private final InspectionCenterErrorCode errorCode;

    public InspectionCenterException(InspectionCenterErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public InspectionCenterException(InspectionCenterErrorCode errorCode, String detail) {
        super(errorCode.getStatus(), errorCode.getMessage() + ": " + detail, errorCode.getField());
        this.errorCode = errorCode;
    }

    @Override
    public String getType() {
        return errorCode.getCode();
    }
}
