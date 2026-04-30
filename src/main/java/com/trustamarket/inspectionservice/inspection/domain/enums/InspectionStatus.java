package com.trustamarket.inspectionservice.inspection.domain.enums;

public enum InspectionStatus {
    REQUESTED,
    ARRIVED,
    IN_PROGRESS,
    PRICED,
    ACCEPTED,
    REJECTED,
    FAILED,
    RETURN_COMPLETED;

    public boolean isTerminal() {
        return this == ACCEPTED || this == REJECTED || this == RETURN_COMPLETED;
    }
}
