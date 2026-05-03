package com.trustamarket.inspectionservice.center.domain.exception;

import com.trustamarket.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum InspectionCenterErrorCode implements ErrorCodeSpec {

    DUPLICATE_CENTER("IC-001", HttpStatus.CONFLICT, "이미 존재하는 검사 센터입니다", ""),
    CENTER_NOT_FOUND("IC-002", HttpStatus.NOT_FOUND, "존재하지 않는 센터입니다", ""),
    INVALID_STATUS_TRANSITION("IC-003", HttpStatus.BAD_REQUEST, "허용되지 않는 상태 전이입니다", "status"),
    CENTER_NOT_DELETABLE("IC-004", HttpStatus.BAD_REQUEST, "센터 삭제는 CLOSED 상태에서만 가능합니다", "status"),
    INVALID_CONTACT_PHONE("IC-005", HttpStatus.BAD_REQUEST, "contactPhone은 비어있을 수 없습니다", "contactPhone"),
    INVALID_CENTER_NAME("IC-006", HttpStatus.BAD_REQUEST, "센터명(name)은 비어있을 수 없습니다", "name"),
    INVALID_ADDRESS_LINE1("IC-007", HttpStatus.BAD_REQUEST, "주소(line1)는 비어있을 수 없습니다", "addressLine1"),
    INVALID_CITY("IC-008", HttpStatus.BAD_REQUEST, "도시(city)는 비어있을 수 없습니다", "city"),
    INVALID_POSTAL_CODE("IC-009", HttpStatus.BAD_REQUEST, "우편번호(postalCode)는 비어있을 수 없습니다", "postalCode"),
    INVALID_CENTER_ID("IC-010", HttpStatus.BAD_REQUEST, "CenterId는 null일 수 없습니다", "");

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final String field;

    InspectionCenterErrorCode(String code, HttpStatus status, String message, String field) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.field = field;
    }

    @Override public String getCode()    { return code; }
    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage() { return message; }
    @Override public String getField()   { return field; }
}
