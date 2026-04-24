package com.trustamarket.inspectionservice.center.domain.model.exception;

// TODO: 추후에 common 내의 BassException 상속 구조로 변경
public class InspectionCenterException extends RuntimeException {

    public InspectionCenterException(String message) {
        super(message);
    }
}
