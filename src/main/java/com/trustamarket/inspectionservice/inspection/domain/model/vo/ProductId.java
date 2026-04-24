package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value, "ProductId 값은 필수입니다");
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }
}
