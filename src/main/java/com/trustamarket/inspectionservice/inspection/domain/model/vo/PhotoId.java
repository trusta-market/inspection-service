package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import java.util.Objects;
import java.util.UUID;

public record PhotoId(UUID value) {

    public PhotoId {
        Objects.requireNonNull(value, "PhotoId 값은 필수입니다");
    }

    public static PhotoId generate() {
        return new PhotoId(UUID.randomUUID());
    }

    public static PhotoId of(UUID value) {
        return new PhotoId(value);
    }
}
