package com.trustamarket.inspectionservice.inspection.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record InspectionId(UUID value) {
    public InspectionId {
        Objects.requireNonNull(value, "InspectionId 값은 필수입니다");
    }

    public static InspectionId generate() {
        return new InspectionId(UUID.randomUUID());
    }

    public static InspectionId of(UUID value) {
        return new InspectionId(value);
    }
}
