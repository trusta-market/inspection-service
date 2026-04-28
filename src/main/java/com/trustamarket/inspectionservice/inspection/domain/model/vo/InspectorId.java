package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public record InspectorId(UUID value) {

    public InspectorId {
        Objects.requireNonNull(value, "InspectorId 값은 필수입니다");
    }

    public static InspectorId of(UUID value) {
        return new InspectorId(value);
    }
}
