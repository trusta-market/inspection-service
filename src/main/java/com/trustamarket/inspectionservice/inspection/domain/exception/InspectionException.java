package com.trustamarket.inspectionservice.inspection.domain.exception;

import com.trustamarket.common.exception.CustomException;

public class InspectionException extends CustomException {

    private final InspectionErrorCode errorCode;

    public InspectionException(InspectionErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public InspectionException(InspectionErrorCode errorCode, String detail) {
        super(errorCode.getStatus(), errorCode.getMessage() + ": " + detail, errorCode.getField());
        this.errorCode = errorCode;
    }

    @Override
    public String getType() {
        return errorCode.getCode();
    }

    public InspectionErrorCode getErrorCode() {
        return errorCode;
    }
}
