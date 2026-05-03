package com.trustamarket.inspectionservice.center.domain.exception;

public class InspectionCenterException extends RuntimeException {

    private final InspectionCenterErrorCode errorCode;

    public InspectionCenterException(InspectionCenterErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InspectionCenterException(InspectionCenterErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.errorCode = errorCode;
    }

    public InspectionCenterErrorCode getErrorCode() {
        return errorCode;
    }
}
