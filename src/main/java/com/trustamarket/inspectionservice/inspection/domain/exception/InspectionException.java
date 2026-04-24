package com.trustamarket.inspectionservice.inspection.domain.exception;

// TODO: 추후에 common 내의 BassException 상속 구조로 변경
public class InspectionException extends RuntimeException {

    public InspectionException(String message) {
        super(message);
    }
}
