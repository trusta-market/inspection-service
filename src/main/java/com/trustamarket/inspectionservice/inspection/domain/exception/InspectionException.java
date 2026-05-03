package com.trustamarket.inspectionservice.inspection.domain.exception;

// TODO: 추후에 common 내의 BaseException 상속 구조로 변경
public class InspectionException extends RuntimeException {

    private final InspectionErrorCode errorCode;

    public InspectionException(InspectionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InspectionException(InspectionErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.errorCode = errorCode;
    }

    public InspectionErrorCode getErrorCode() {
        return errorCode;
    }
}
