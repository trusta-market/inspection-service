package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public record SellerId(UUID value) {

    public SellerId {
        Objects.requireNonNull(value, "SellerId 값은 필수입니다");
    }

    public static SellerId of(UUID value) {
        return new SellerId(value);
    }
}
