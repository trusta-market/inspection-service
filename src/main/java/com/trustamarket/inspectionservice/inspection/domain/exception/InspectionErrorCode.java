package com.trustamarket.inspectionservice.inspection.domain.exception;

import com.trustamarket.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum InspectionErrorCode implements ErrorCodeSpec {

    INSPECTION_NOT_FOUND("IN-001", HttpStatus.NOT_FOUND, "검수 요청을 찾을 수 없습니다", ""),
    INVALID_STATUS_TRANSITION("IN-002", HttpStatus.BAD_REQUEST, "현재 상태에서 허용되지 않는 작업입니다", "status"),
    INSPECTOR_NOTE_REQUIRED("IN-003", HttpStatus.BAD_REQUEST, "검수 실패 시 검수 노트는 필수입니다", "inspectorNote"),
    PHOTO_NOT_FOUND("IN-004", HttpStatus.NOT_FOUND, "해당 사진이 없습니다", ""),
    PHOTO_TYPE_NOT_ALLOWED("IN-005", HttpStatus.BAD_REQUEST, "현재 상태에서 허용되지 않는 사진 타입입니다", "photoType"),
    INVALID_PHOTO_URL("IN-006", HttpStatus.BAD_REQUEST, "사진 URL은 비어있을 수 없습니다", "url"),
    INVALID_DISPLAY_ORDER("IN-007", HttpStatus.BAD_REQUEST, "displayOrder는 0 이상이어야 합니다", "displayOrder"),
    INVALID_PHOTO_ID("IN-008", HttpStatus.BAD_REQUEST, "PhotoId는 null일 수 없습니다", ""),
    INVALID_INSPECTOR_ID("IN-009", HttpStatus.BAD_REQUEST, "InspectorId는 null일 수 없습니다", ""),
    INVALID_SELLER_ID("IN-010", HttpStatus.BAD_REQUEST, "SellerId는 null일 수 없습니다", ""),
    INVALID_INSPECTION_ID("IN-011", HttpStatus.BAD_REQUEST, "InspectionId는 null일 수 없습니다", ""),
    INVALID_PRODUCT_ID("IN-012", HttpStatus.BAD_REQUEST, "ProductId는 null일 수 없습니다", ""),
    INVALID_MONEY_AMOUNT("IN-013", HttpStatus.BAD_REQUEST, "금액(amount)은 음수일 수 없습니다", "amount");

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final String field;

    InspectionErrorCode(String code, HttpStatus status, String message, String field) {
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
