package com.trustamarket.inspectionservice.inspection.domain.enums;

public enum InspectionStatus {
    REQUESTED,
    ARRIVED,
    IN_PROGRESS,
    PRICED,
    PRICE_ACCEPTED,
    PRICE_REJECTED,
    FAILED,
    RETURN_COMPLETED;

    public boolean isTerminal() {
        return this == PRICE_ACCEPTED || this == RETURN_COMPLETED;
    }
}
