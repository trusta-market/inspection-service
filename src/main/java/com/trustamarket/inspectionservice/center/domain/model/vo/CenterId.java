package com.trustamarket.inspectionservice.center.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public record CenterId(UUID value) {

    public CenterId {
        Objects.requireNonNull(value, "CenterId 값은 필수입니다");
    }

    public static CenterId generate() {
        return new CenterId(UUID.randomUUID());
    }

    public static CenterId of(UUID value) {
        return new CenterId(value);
    }
}
