package com.trustamarket.inspectionservice.inspection.domain.enums;

public enum InspectionStatus {
    REQUESTED,
    ARRIVED,
    IN_PROGRESS,
    PRICED,
    FAILED,
    RETURN_COMPLETED;

    public boolean isTerminal() {
        return this == PRICED || this == RETURN_COMPLETED;
    }
}
